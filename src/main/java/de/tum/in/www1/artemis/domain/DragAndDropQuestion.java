package de.tum.in.www1.artemis.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A DragAndDropQuestion.
 */
@Entity
@Table(name = "drag_and_drop_question")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DragAndDropQuestion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "background_file_path")
    private String backgroundFilePath;

    @OneToMany(mappedBy = "question")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<DropLocation> dropLocations = new HashSet<>();

    @OneToMany(mappedBy = "question")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<DragItem> dragItems = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBackgroundFilePath() {
        return backgroundFilePath;
    }

    public DragAndDropQuestion backgroundFilePath(String backgroundFilePath) {
        this.backgroundFilePath = backgroundFilePath;
        return this;
    }

    public void setBackgroundFilePath(String backgroundFilePath) {
        this.backgroundFilePath = backgroundFilePath;
    }

    public Set<DropLocation> getDropLocations() {
        return dropLocations;
    }

    public DragAndDropQuestion dropLocations(Set<DropLocation> dropLocations) {
        this.dropLocations = dropLocations;
        return this;
    }

    public DragAndDropQuestion addDropLocations(DropLocation dropLocation) {
        this.dropLocations.add(dropLocation);
        dropLocation.setQuestion(this);
        return this;
    }

    public DragAndDropQuestion removeDropLocations(DropLocation dropLocation) {
        this.dropLocations.remove(dropLocation);
        dropLocation.setQuestion(null);
        return this;
    }

    public void setDropLocations(Set<DropLocation> dropLocations) {
        this.dropLocations = dropLocations;
    }

    public Set<DragItem> getDragItems() {
        return dragItems;
    }

    public DragAndDropQuestion dragItems(Set<DragItem> dragItems) {
        this.dragItems = dragItems;
        return this;
    }

    public DragAndDropQuestion addDragItems(DragItem dragItem) {
        this.dragItems.add(dragItem);
        dragItem.setQuestion(this);
        return this;
    }

    public DragAndDropQuestion removeDragItems(DragItem dragItem) {
        this.dragItems.remove(dragItem);
        dragItem.setQuestion(null);
        return this;
    }

    public void setDragItems(Set<DragItem> dragItems) {
        this.dragItems = dragItems;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DragAndDropQuestion dragAndDropQuestion = (DragAndDropQuestion) o;
        if (dragAndDropQuestion.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), dragAndDropQuestion.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "DragAndDropQuestion{" +
            "id=" + getId() +
            ", backgroundFilePath='" + getBackgroundFilePath() + "'" +
            "}";
    }
}
