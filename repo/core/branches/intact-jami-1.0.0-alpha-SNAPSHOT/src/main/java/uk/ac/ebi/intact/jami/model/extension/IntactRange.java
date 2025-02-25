package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Target;
import org.hibernate.annotations.Type;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.model.Entity;
import psidev.psi.mi.jami.utils.comparator.range.UnambiguousRangeAndResultingSequenceComparator;
import uk.ac.ebi.intact.jami.model.AbstractIntactPrimaryObject;

import javax.persistence.*;

/**
 * Intact implementation of range
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>10/01/14</pre>
 */
@javax.persistence.Entity
@Table(name = "ia_range")
public class IntactRange extends AbstractIntactPrimaryObject implements Range{

    private Position start;
    private Position end;
    private boolean isLink;

    private ResultingSequence resultingSequence;
    private Entity participant;

    protected IntactRange(){

    }

    public IntactRange(Position start, Position end){
        setPositions(start, end);
    }

    public IntactRange(Position start, Position end, boolean isLink){
        this(start, end);
        this.isLink = isLink;
    }

    public IntactRange(Position start, Position end, ResultingSequence resultingSequence){
        this(start, end);
        this.resultingSequence = resultingSequence;
    }

    public IntactRange(Position start, Position end, boolean isLink, ResultingSequence resultingSequence){
        this(start, end, isLink);
        this.resultingSequence = resultingSequence;
    }

    public IntactRange(Position start, Position end, Entity participant){
        this(start, end);
        this.participant = participant;
    }

    public IntactRange(Position start, Position end, boolean isLink, Entity participant){
        this(start, end, isLink);
        this.participant = participant;
    }

    @Embedded
    @AttributeOverrides( {
            @AttributeOverride(name="start", column = @Column(name="fromintervalstart") ),
            @AttributeOverride(name="end", column = @Column(name="fromintervalend") )
    } )
    @AssociationOverrides( { @AssociationOverride(name = "status", joinColumns = @JoinColumn(name = "fromfuzzytype_ac")) })
    @Target(IntactPosition.class)
    public Position getStart() {
        return this.start;
    }

    @Embedded
    @AttributeOverrides( {
            @AttributeOverride(name="start", column = @Column(name="tointervalstart") ),
            @AttributeOverride(name="end", column = @Column(name="tointervalend") )
    } )
    @AssociationOverrides( { @AssociationOverride(name = "status", joinColumns = @JoinColumn(name = "tofuzzytype_ac")) })
    @Target(IntactPosition.class)
    public Position getEnd() {
        return this.end;
    }

    public void setPositions(Position start, Position end) {
        if (start == null){
            throw new IllegalArgumentException("The start position is required and cannot be null");
        }
        if (end == null){
            throw new IllegalArgumentException("The end position is required and cannot be null");
        }

        if (start.getEnd() != 0 && end.getStart() != 0 && start.getEnd() > end.getStart()){
            throw new IllegalArgumentException("The start position cannot be ending before the end position");
        }
        this.start = start;
        this.end = end;
    }

    @Column( name = "link" )
    @Type( type = "yes_no" )
    public boolean isLink() {
        return this.isLink;
    }

    public void setLink(boolean link) {
        this.isLink = link;
    }

    @Embedded
    @Target(IntactResultingSequence.class)
    public ResultingSequence getResultingSequence() {
        return this.resultingSequence;
    }

    public void setResultingSequence(ResultingSequence resultingSequence) {
        this.resultingSequence = resultingSequence;
    }

    @ManyToOne(targetEntity = AbstractIntactEntity.class)
    @JoinColumn(name = "participant_ac", referencedColumnName = "ac")
    @Target(AbstractIntactEntity.class)
    public Entity getParticipant() {
        return this.participant;
    }

    public void setParticipant(Entity participant) {
        this.participant = participant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }

        if (!(o instanceof Range)){
            return false;
        }

        return UnambiguousRangeAndResultingSequenceComparator.areEquals(this, (Range) o);
    }

    @Override
    public int hashCode() {
        return UnambiguousRangeAndResultingSequenceComparator.hashCode(this);
    }

    @Override
    public String toString() {
        return start.toString() + " - " + end.toString() + (isLink ? "(linked)" : "");
    }

    private void setStart(Position start) {
        this.start = start;
    }

    private void setEnd(Position end) {
        this.end = end;
    }
}
