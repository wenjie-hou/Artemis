import { NgModule } from '@angular/core';
import { ArtemisCodeEditorModule } from 'app/exercises/programming/shared/code-editor/code-editor.module';
import { ArtemisProgrammingParticipationRoutingModule } from 'app/exercises/programming/participate/programming-participation-routing.module';
import { CodeEditorStudentContainerComponent } from 'app/exercises/programming/participate/code-editor-student-container.component';
import { ArtemisSharedModule } from 'app/shared/shared.module';
import { ArtemisProgrammingExerciseActionsModule } from 'app/exercises/programming/shared/actions/programming-exercise-actions.module';
import { ArtemisExerciseHintModule } from 'app/exercises/shared/exercise-hint/exercise-hint.module';
import { ArtemisResultModule } from 'app/exercises/shared/result/result.module';

@NgModule({
    imports: [
        ArtemisSharedModule,
        ArtemisProgrammingParticipationRoutingModule,
        ArtemisCodeEditorModule,
        ArtemisProgrammingExerciseActionsModule,
        ArtemisExerciseHintModule,
        ArtemisResultModule,
    ],
    declarations: [CodeEditorStudentContainerComponent],
})
export class ArtemisProgrammingParticipationModule {}