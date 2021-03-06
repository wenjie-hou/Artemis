import { NgModule } from '@angular/core';
import { ArtemisSharedModule } from 'app/shared/shared.module';
import { DragAndDropQuestionComponent } from 'app/exercises/quiz/shared/questions/drag-and-drop-question/drag-and-drop-question.component';
import { MultipleChoiceQuestionComponent } from 'app/exercises/quiz/shared/questions/multiple-choice-question/multiple-choice-question.component';
import { ShortAnswerQuestionComponent } from 'app/exercises/quiz/shared/questions/short-answer-question/short-answer-question.component';
import { DragItemComponent } from './drag-and-drop-question/drag-item.component';
import { DeviceDetectorService } from 'ngx-device-detector';
import { AngularFittextModule } from 'angular-fittext';
import { DndModule } from 'ng2-dnd';
import { QuizScoringInfoStudentModalComponent } from 'app/exercises/quiz/shared/questions/quiz-scoring-infostudent-modal/quiz-scoring-info-student-modal.component';

@NgModule({
    imports: [ArtemisSharedModule, AngularFittextModule, DndModule.forRoot()],
    declarations: [DragItemComponent, DragAndDropQuestionComponent, MultipleChoiceQuestionComponent, ShortAnswerQuestionComponent, QuizScoringInfoStudentModalComponent],
    providers: [DeviceDetectorService],
    exports: [DragItemComponent, DragAndDropQuestionComponent, MultipleChoiceQuestionComponent, ShortAnswerQuestionComponent],
})
export class ArtemisQuizQuestionTypesModule {}
