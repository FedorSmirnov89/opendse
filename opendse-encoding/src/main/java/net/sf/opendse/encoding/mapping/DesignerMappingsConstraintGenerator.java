package net.sf.opendse.encoding.mapping;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Constraint.Operator;

import net.sf.opendse.encoding.variables.M;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

/**
 * The {@link DesignerMappingsConstraintGenerator} ensures that exactly one of
 * the mappings provided by the designer is activated for each of the
 * corresponding processes (if the processes are activated).
 * 
 * @author Fedor Smirnov
 *
 */
public class DesignerMappingsConstraintGenerator implements MappingConstraintGenerator {

	@Override
	public Set<MappingVariable> toConstraints(Set<T> processVariables, Mappings<Task, Resource> mappings,
			Set<Constraint> constraints) {
		Set<MappingVariable> result = new HashSet<MappingVariable>();
		// iterate the processes
		for (T processVariable : processVariables) {
			Task task = processVariable.getTask();
			if (mappings.get(task).isEmpty()) {
				throw new IllegalArgumentException("No mappings provided for the process " + task.getId());
			}
			result.addAll(formulateMappingConstraint(processVariable, mappings.get(task), constraints));
		}
		return result;
	}

	/**
	 * Formulates the constraint stating that exactly one of the provided mappings has to be active in the cases where the process is active.
	 * 
	 * not(T) + sum(M) = 1
	 * 
	 * @param processVariable
	 * @param mappings
	 * @param constraints
	 * @return the mapping variables created during the constraint formulation
	 */
	protected Set<MappingVariable> formulateMappingConstraint(T processVariable, Set<Mapping<Task, Resource>> mappings, Set<Constraint> constraints) {
		Constraint constraint = new Constraint(Operator.EQ, 1);
		constraint.add(Variables.n(processVariable));
		Set<MappingVariable> result = new HashSet<MappingVariable>();
		for (Mapping<Task, Resource> mapping : mappings) {
			M mVar = Variables.var(mapping); 
			result.add(mVar);
			constraint.add(Variables.p(mVar));
		}
		constraints.add(constraint);
		return result;
	}
}
