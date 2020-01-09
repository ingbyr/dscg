package com.ingbyr.dscg;

import com.google.common.collect.Sets;
import com.ingbyr.hwsc.common.Concept;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class State {

    public Set<Concept> concepts;

    @EqualsAndHashCode.Exclude
    public
    int earliestTime;

    public State(Set<Concept> concepts) {
        this.concepts = concepts;
    }

    public State copy() {
        Set<Concept> conceptsCopy = Sets.newHashSet(concepts);
        return new State(conceptsCopy, earliestTime);
    }

    @Override
    public String toString() {
        return "S@" + earliestTime + '(' + concepts.hashCode() + ')';
//        return "S@" + earliestTime + '(' + concepts + ')';
    }
}
