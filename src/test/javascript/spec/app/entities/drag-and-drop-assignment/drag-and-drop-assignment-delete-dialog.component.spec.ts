/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs/Observable';
import { JhiEventManager } from 'ng-jhipster';

import { ArTeMiSTestModule } from '../../../test.module';
import { DragAndDropAssignmentDeleteDialogComponent } from '../../../../../../main/webapp/app/entities/drag-and-drop-assignment/drag-and-drop-assignment-delete-dialog.component';
import { DragAndDropAssignmentService } from '../../../../../../main/webapp/app/entities/drag-and-drop-assignment/drag-and-drop-assignment.service';

describe('Component Tests', () => {

    describe('DragAndDropAssignment Management Delete Component', () => {
        let comp: DragAndDropAssignmentDeleteDialogComponent;
        let fixture: ComponentFixture<DragAndDropAssignmentDeleteDialogComponent>;
        let service: DragAndDropAssignmentService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [ArTeMiSTestModule],
                declarations: [DragAndDropAssignmentDeleteDialogComponent],
                providers: [
                    DragAndDropAssignmentService
                ]
            })
            .overrideTemplate(DragAndDropAssignmentDeleteDialogComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(DragAndDropAssignmentDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(DragAndDropAssignmentService);
            mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
            mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
        });

        describe('confirmDelete', () => {
            it('Should call delete service on confirmDelete',
                inject([],
                    fakeAsync(() => {
                        // GIVEN
                        spyOn(service, 'delete').and.returnValue(Observable.of({}));

                        // WHEN
                        comp.confirmDelete(123);
                        tick();

                        // THEN
                        expect(service.delete).toHaveBeenCalledWith(123);
                        expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                        expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
                    })
                )
            );
        });
    });

});
