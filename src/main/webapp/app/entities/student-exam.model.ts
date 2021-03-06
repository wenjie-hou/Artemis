import { Moment } from 'moment';
import { User } from 'app/core/user/user.model';
import { Exam } from 'app/entities/exam.model';
import { Exercise } from 'app/entities/exercise.model';
import { BaseEntity } from 'app/shared/model/base-entity';
import { ExamSession } from 'app/entities/exam-session.model';

export class StudentExam implements BaseEntity {
    public id: number;
    public workingTime: number;
    public submitted: boolean;
    public started: boolean;
    public submissionDate: Moment | null;
    public user: User;
    public exam: Exam;
    public exercises: Exercise[];
    public examSessions: ExamSession[];

    // helper attributes (calculated by the server at the time of the last request)
    public ended = false;
}
