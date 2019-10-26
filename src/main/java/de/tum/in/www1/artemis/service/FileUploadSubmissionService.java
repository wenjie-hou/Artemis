package de.tum.in.www1.artemis.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import de.tum.in.www1.artemis.domain.*;
import de.tum.in.www1.artemis.domain.enumeration.AssessmentType;
import de.tum.in.www1.artemis.repository.FileUploadSubmissionRepository;
import de.tum.in.www1.artemis.repository.ResultRepository;
import de.tum.in.www1.artemis.repository.StudentParticipationRepository;
import de.tum.in.www1.artemis.repository.SubmissionRepository;
import de.tum.in.www1.artemis.web.rest.errors.EntityNotFoundException;

@Service
@Transactional
public class FileUploadSubmissionService extends SubmissionService<FileUploadSubmission, FileUploadSubmissionRepository> {

    private final Logger log = LoggerFactory.getLogger(FileUploadSubmissionService.class);

    private final ResultService resultService;

    private final FileService fileService;

    public FileUploadSubmissionService(FileUploadSubmissionRepository fileUploadSubmissionRepository, SubmissionRepository submissionRepository, ResultRepository resultRepository,
            ParticipationService participationService, UserService userService, StudentParticipationRepository studentParticipationRepository,
            SimpMessageSendingOperations messagingTemplate, ResultService resultService, FileService fileService, AuthorizationCheckService authCheckService) {
        super(submissionRepository, userService, authCheckService, resultRepository, participationService, messagingTemplate, studentParticipationRepository,
                fileUploadSubmissionRepository);
        this.resultService = resultService;
        this.fileService = fileService;
    }

    /**
     * Handles file upload submissions sent from the client and saves them in the database.
     *
     * @param fileUploadSubmission the file upload submission that should be saved
     * @param fileUploadExercise   the corresponding file upload exercise
     * @param file                  the file that will be stored on the server
     * @param principal            the user principal
     * @return the saved file upload submission
     * @throws IOException if file can't be saved
     */
    @Transactional(rollbackFor = Exception.class)
    public FileUploadSubmission handleFileUploadSubmission(FileUploadSubmission fileUploadSubmission, MultipartFile file, FileUploadExercise fileUploadExercise,
            Principal principal) throws IOException {
        fileUploadSubmission = save(fileUploadSubmission, fileUploadExercise, principal.getName(), FileUploadSubmission.class);
        final var localPath = saveFileForSubmission(file, fileUploadSubmission, fileUploadExercise);
        fileUploadSubmission.setFilePath(fileService.publicPathForActualPath(localPath, fileUploadSubmission.getId()));
        genericSubmissionRepository.save(fileUploadSubmission);
        return fileUploadSubmission;
    }

    /**
     * Given an exercise id, find a random file upload submission for that exercise which still doesn't have any manual result. No manual result means that no user has started an
     * assessment for the corresponding submission yet.
     *
     * @param fileUploadExercise the exercise for which we want to retrieve a submission without manual result
     * @return a fileUploadSubmission without any manual result or an empty Optional if no submission without manual result could be found
     */
    @Transactional(readOnly = true)
    public Optional<FileUploadSubmission> getFileUploadSubmissionWithoutManualResult(FileUploadExercise fileUploadExercise) {
        return getRandomUnassessedSubmission(fileUploadExercise, FileUploadSubmission.class);
    }

    private String saveFileForSubmission(final MultipartFile file, final Submission submission, FileUploadExercise exercise) throws IOException {
        final var exerciseId = exercise.getId();
        final var submissionId = submission.getId();
        final var filename = file.getOriginalFilename().replaceAll("\\s", "");
        final var dirPath = FileUploadSubmission.buildFilePath(exerciseId, submissionId);
        final var filePath = dirPath + filename;
        final var savedFile = new java.io.File(filePath);
        final var dir = new java.io.File(dirPath);

        if (!dir.exists()) {
            dir.mkdirs();
        }
        Files.copy(file.getInputStream(), savedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        return filePath;
    }

    /**
     * Soft lock the submission to prevent other tutors from receiving and assessing it.  We set the assessor and save the result to soft lock the assessment in the client. If no result exists for this submission we create one first.
     *
     * @param fileUploadSubmission the submission to lock
     */
    private void lockSubmission(FileUploadSubmission fileUploadSubmission) {
        Result result = fileUploadSubmission.getResult();
        if (result == null) {
            result = setNewResult(fileUploadSubmission);
        }

        if (result.getAssessor() == null) {
            resultService.setAssessor(result);
        }

        result.setAssessmentType(AssessmentType.MANUAL);
        resultRepository.save(result);
        log.debug("Assessment locked with result id: " + result.getId() + " for assessor: " + result.getAssessor().getFirstName());
    }

    /**
     * Get the file upload submission with the given ID from the database and lock the submission to prevent other tutors from receiving and assessing it. Additionally, check if the
     * submission lock limit has been reached.
     *
     * @param submissionId       the id of the file upload submission
     * @param fileUploadExercise the corresponding exercise
     * @return the locked file upload submission
     */
    @Transactional
    public FileUploadSubmission getLockedFileUploadSubmission(Long submissionId, FileUploadExercise fileUploadExercise) {
        FileUploadSubmission fileUploadSubmission = findOneWithEagerResultAndFeedbackAndAssessorAndParticipationResults(submissionId);

        if (fileUploadSubmission.getResult() == null || fileUploadSubmission.getResult().getAssessor() == null) {
            checkSubmissionLockLimit(fileUploadExercise.getCourse().getId());
        }

        lockSubmission(fileUploadSubmission);
        return fileUploadSubmission;
    }

    /**
     * Get a file upload submission of the given exercise that still needs to be assessed and lock the submission to prevent other tutors from receiving and assessing it.
     *
     * @param fileUploadExercise the exercise the submission should belong to
     * @return a locked file upload submission that needs an assessment
     */
    @Transactional
    public FileUploadSubmission getLockedFileUploadSubmissionWithoutResult(FileUploadExercise fileUploadExercise) {
        FileUploadSubmission fileUploadSubmission = getFileUploadSubmissionWithoutManualResult(fileUploadExercise)
                .orElseThrow(() -> new EntityNotFoundException("File upload submission for exercise " + fileUploadExercise.getId() + " could not be found"));
        lockSubmission(fileUploadSubmission);
        return fileUploadSubmission;
    }

    /**
     * Get the file upload submission with the given id from the database. The submission is loaded together with its result, the feedback of the result, the assessor of the result,
     * its participation and all results of the participation. Throws an EntityNotFoundException if no submission could be found for the given id.
     *
     * @param submissionId the id of the submission that should be loaded from the database
     * @return the file upload submission with the given id
     */
    private FileUploadSubmission findOneWithEagerResultAndFeedbackAndAssessorAndParticipationResults(Long submissionId) {
        return genericSubmissionRepository.findWithEagerResultAndFeedbackAndAssessorAndParticipationResultsById(submissionId)
                .orElseThrow(() -> new EntityNotFoundException("File Upload submission with id \"" + submissionId + "\" does not exist"));
    }
}
