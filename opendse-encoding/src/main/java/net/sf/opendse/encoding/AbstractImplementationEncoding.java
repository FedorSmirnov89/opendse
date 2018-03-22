package net.sf.opendse.encoding;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Literal;

import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.encoding.variables.AllocationVariable;
import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.InterfaceVariable;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.RoutingVariable;

/**
 * Parent of all implementation encodings. Details the encoding flow and the
 * information exchange between the encoding modules.
 * 
 * @author Fedor Smirnov
 *
 */
public abstract class AbstractImplementationEncoding implements ImplementationEncoding {
	protected final ApplicationEncoding applicationEncoding;
	protected final MappingEncoding mappingEncoding;
	protected final RoutingEncoding routingEncoding;
	protected final AllocationEncoding allocationEncoding;

	protected final Set<ApplicationVariable> applicationVariables;
	protected final Set<MappingVariable> mappingVariables;
	protected final Set<RoutingVariable> routingVariables;
	protected final Set<AllocationVariable> allocationVariables;
	protected boolean encodingFinished = false;

	public AbstractImplementationEncoding(ApplicationEncoding applicationEncoding, MappingEncoding mappingEncoding,
			RoutingEncoding routingEncoding, AllocationEncoding allocationEncoding) {

		this.applicationEncoding = applicationEncoding;
		this.mappingEncoding = mappingEncoding;
		this.routingEncoding = routingEncoding;
		this.allocationEncoding = allocationEncoding;

		this.applicationVariables = new HashSet<ApplicationVariable>();
		this.mappingVariables = new HashSet<MappingVariable>();
		this.routingVariables = new HashSet<RoutingVariable>();
		this.allocationVariables = new HashSet<AllocationVariable>();
	}

	@SuppressWarnings("unchecked")
	@Override
	public final Set<Constraint> toConstraints(Specification specification) {
		Application<Task, Dependency> application = specification.getApplication();
		Mappings<Task, Resource> mappings = specification.getMappings();
		Routings<Task, Resource, Link> routings = specification.getRoutings();
		Architecture<Resource, Link> architecture = specification.getArchitecture();

		Set<Constraint> applicationConstraints = applicationEncoding.toConstraints(application);
		applicationVariables.addAll((Set<ApplicationVariable>) (Set<?>) extractVariables(applicationConstraints,
				ApplicationVariable.class));
		Set<Constraint> mappingConstraints = mappingEncoding.toConstraints(mappings, applicationVariables);
		mappingVariables
				.addAll((Set<MappingVariable>) (Set<?>) extractVariables(mappingConstraints, MappingVariable.class));
		Set<Constraint> routingConstraints = routingEncoding.toConstraints(applicationVariables, mappingVariables,
				routings);
		routingVariables
				.addAll((Set<RoutingVariable>) (Set<?>) extractVariables(routingConstraints, RoutingVariable.class));
		Set<Constraint> allocationConstraints = allocationEncoding.toConstraints(mappingVariables, routingVariables,
				architecture);
		allocationVariables.addAll(
				(Set<AllocationVariable>) (Set<?>) extractVariables(allocationConstraints, AllocationVariable.class));
		
		Set<Constraint> result = new HashSet<Constraint>();
		result.addAll(applicationConstraints);
		result.addAll(mappingConstraints);
		result.addAll(routingConstraints);
		result.addAll(formulateAdditionalConstraints());
		encodingFinished = true;
		return result;
	}

	@Override
	public Set<InterfaceVariable> getInterfaceVariables() {
		if (!encodingFinished) {
			throw new IllegalArgumentException("The interface variables are not yet encoded.");
		}
		Set<InterfaceVariable> result = new HashSet<InterfaceVariable>();
		result.addAll(applicationVariables);
		result.addAll(mappingVariables);
		result.addAll(routingVariables);
		result.addAll(allocationVariables);
		return result;
	}

	/**
	 * perform the necessary preprocessing
	 */
	protected abstract void preprocessSpecification();

	/**
	 * Adds additional constraints.
	 *
	 * @return the set of additional constraints
	 */
	protected abstract Set<Constraint> formulateAdditionalConstraints();

	/**
	 * Extracts the {@code InterfaceVariable}s of a certain type from the given
	 * constraint set.
	 * 
	 * @param constraints
	 *            the set of constraints encoding the variables
	 * @param variableClass
	 *            the class of the variables that are to be extracted
	 * @return the set of interface variables extracted from the constraints
	 */
	protected Set<InterfaceVariable> extractVariables(Set<Constraint> constraints, Class<?> variableClass) {
		Set<InterfaceVariable> result = new HashSet<InterfaceVariable>();
		for (Constraint constraint : constraints) {
			for (Literal literal : constraint.getLiterals()) {
				if (variableClass.isAssignableFrom(literal.variable().getClass())) {
					result.add((InterfaceVariable) literal.variable());
				}
			}
		}
		return result;
	}
}
