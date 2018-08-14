package net.sf.opendse.encoding.routing;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.DDLRR;
import net.sf.opendse.encoding.variables.DTT;
import net.sf.opendse.encoding.variables.M;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Task;
import verification.ConstraintVerifier;

public class EdgeEncoderRedundantTest {

	@Test
	public void test2() {
		Task t0 = new Task("t0");
		Task t1 = new Task("t1");
		T tVar0 = Variables.varT(t0);
		T tVar1 = Variables.varT(t1);
		Communication c = new Communication("comm");
		T tVarC = Variables.varT(c);
		Dependency d0 = new Dependency("d0");
		Dependency d1 = new Dependency("d1");
		DTT dtt0 = Variables.varDTT(d0, t0, c);
		DTT dtt1 = Variables.varDTT(d1, c, t1);
		CommunicationFlow flow = new CommunicationFlow(dtt0, dtt1);
		Set<ApplicationVariable> applicationVariables = new HashSet<ApplicationVariable>();
		applicationVariables.add(tVar0);
		applicationVariables.add(tVar1);
		applicationVariables.add(tVarC);
		applicationVariables.add(dtt0);
		applicationVariables.add(dtt1);

		Resource r0 = new Resource("r0");
		Resource r1 = new Resource("r1");
		Resource r2 = new Resource("r2");
		Resource r3 = new Resource("r3");
		Link l0 = new Link("l0");
		Link l1 = new Link("l1");
		Link l2 = new Link("l2");
		Link l3 = new Link("l3");
		Architecture<Resource, Link> routing = new Architecture<Resource, Link>();
		routing.addEdge(l0, r0, r1, EdgeType.UNDIRECTED);
		routing.addEdge(l1, r0, r2, EdgeType.UNDIRECTED);
		routing.addEdge(l2, r1, r3, EdgeType.UNDIRECTED);
		routing.addEdge(l3, r2, r3, EdgeType.UNDIRECTED);
		Routings<Task, Resource, Link> routings = new Routings<Task, Resource, Link>();
		routings.set(c, routing);

		Mapping<Task, Resource> m0 = new Mapping<Task, Resource>("m0", t0, r0);
		Mapping<Task, Resource> m1 = new Mapping<Task, Resource>("m1", t1, r3);
		M mVar0 = Variables.varM(m0);
		M mVar1 = Variables.varM(m1);
		Set<MappingVariable> mappingVars = new HashSet<MappingVariable>();
		mappingVars.add(mVar0);
		mappingVars.add(mVar1);

		CommunicationFlowRoutingManagerInjective commFlowManager = new CommunicationFlowRoutingManagerInjective(
				new ActivationEncoderDefault(), new EndNodeEncoderMapping(), new RoutingResourceEncoderDefault(),
				new RoutingEdgeEncoderRedundant(), new ProxyEncoderCompact());
		CommunicationRoutingManager routingEncoderManager = new CommunicationRoutingManagerDefault(
				new OneDirectionEncoderDefault(), new CycleBreakEncoderOrder(),
				new CommunicationHierarchyEncoderDefault(), commFlowManager, new ProxyEncoderCompact(),
				new AdditionalRoutingConstraintsEncoderMulti());
		RoutingEncodingFlexible routingEncoding = new RoutingEncodingFlexible(routingEncoderManager);

		Set<Constraint> cs = routingEncoding.toConstraints(applicationVariables, mappingVars, routings);
		ConstraintVerifier verifier = new ConstraintVerifier(cs);
		verifier.activateVariable(tVar0);
		verifier.activateVariable(tVar1);
		verifier.activateVariable(dtt0);
		verifier.activateVariable(dtt1);
		verifier.activateVariable(tVarC);
		verifier.activateVariable(mVar0);
		verifier.activateVariable(mVar1);

		verifier.activateVariable(Variables.varDDLRR(flow, l0, r0, r1));
		verifier.activateVariable(Variables.varDDLRR(flow, l1, r0, r2));
		verifier.activateVariable(Variables.varDDLRR(flow, l2, r1, r3));
		verifier.activateVariable(Variables.varDDLRR(flow, l3, r2, r3));
	}

	@Test
	public void test() {
		Task t0 = new Task("t0");
		Communication comm = new Communication("c");
		Task t1 = new Task("t1");
		Dependency d0 = new Dependency("d0");
		Dependency d1 = new Dependency("d1");
		DTT dtt0 = Variables.varDTT(d0, t0, comm);
		DTT dtt1 = Variables.varDTT(d1, comm, t1);
		CommunicationFlow flow = new CommunicationFlow(dtt0, dtt1);

		Architecture<Resource, Link> routing = new Architecture<Resource, Link>();
		Resource r0 = new Resource("r0");
		Resource r1 = new Resource("r1");
		Resource r2 = new Resource("r2");
		Resource r3 = new Resource("r3");
		Resource r4 = new Resource("r4");
		Resource r5 = new Resource("r5");
		Resource r6 = new Resource("r6");
		Resource r7 = new Resource("r7");
		Link l0 = new Link("l0");
		Link l1 = new Link("l1");
		Link l2 = new Link("l2");
		Link l3 = new Link("l3");
		Link l4 = new Link("l4");
		Link l5 = new Link("l5");
		Link l6 = new Link("l6");
		Link l7 = new Link("l7");
		Link l8 = new Link("l8");

		routing.addEdge(l0, r0, r1, EdgeType.UNDIRECTED);
		routing.addEdge(l1, r1, r2, EdgeType.UNDIRECTED);
		routing.addEdge(l2, r1, r3, EdgeType.UNDIRECTED);
		routing.addEdge(l3, r2, r4, EdgeType.UNDIRECTED);
		routing.addEdge(l4, r3, r5, EdgeType.UNDIRECTED);
		routing.addEdge(l5, r4, r6, EdgeType.UNDIRECTED);
		routing.addEdge(l6, r5, r6, EdgeType.UNDIRECTED);
		routing.addEdge(l7, r6, r7, EdgeType.UNDIRECTED);
		routing.addEdge(l8, r5, r2, EdgeType.UNDIRECTED);

		Mapping<Task, Resource> m0 = new Mapping<Task, Resource>("m0", t0, r0);
		Mapping<Task, Resource> m1 = new Mapping<Task, Resource>("m1", t1, r7);
		M mVar0 = Variables.varM(m0);
		M mVar1 = Variables.varM(m1);
		Set<MappingVariable> mappingVars = new HashSet<MappingVariable>();
		mappingVars.add(mVar0);
		mappingVars.add(mVar1);

		DDLRR ddlrr0 = Variables.varDDLRR(flow, l0, r0, r1);
		DDLRR ddlrr1 = Variables.varDDLRR(flow, l1, r1, r2);
		DDLRR ddlrr2 = Variables.varDDLRR(flow, l2, r1, r3);
		DDLRR ddlrr3 = Variables.varDDLRR(flow, l3, r2, r4);
		DDLRR ddlrr4 = Variables.varDDLRR(flow, l4, r3, r5);
		DDLRR ddlrr5 = Variables.varDDLRR(flow, l5, r4, r6);
		DDLRR ddlrr6 = Variables.varDDLRR(flow, l6, r5, r6);
		DDLRR ddlrr7 = Variables.varDDLRR(flow, l7, r6, r7);
		DDLRR ddlrr8_a = Variables.varDDLRR(flow, l8, r5, r2);
		DDLRR ddlrr8_b = Variables.varDDLRR(flow, l8, r2, r5);

		RoutingEdgeEncoderRedundant routingEdgeEncoderRedundant = new RoutingEdgeEncoderRedundant();
		Set<Constraint> cs = routingEdgeEncoderRedundant.toConstraints(flow, routing);
		RoutingResourceEncoderDefault resourceEncoder = new RoutingResourceEncoderDefault();
		cs.addAll(resourceEncoder.toConstraints(flow, routing, mappingVars));
		ConstraintVerifier verifyRouting = new ConstraintVerifier(cs);
		verifyRouting.activateVariable(mVar0);
		verifyRouting.activateVariable(mVar1);
		verifyRouting.activateVariable(ddlrr1);
		verifyRouting.activateVariable(ddlrr2);
		verifyRouting.activateVariable(Variables.varDDsR(flow, r0));
		verifyRouting.deactivateVariable(Variables.varDDdR(flow, r0));
		verifyRouting.activateVariable(Variables.varDDdR(flow, r7));
		verifyRouting.deactivateVariable(Variables.varDDsR(flow, r7));
		verifyRouting.deactivateVariable(Variables.varDDsR(flow, r1));
		verifyRouting.deactivateVariable(Variables.varDDsR(flow, r2));
		verifyRouting.deactivateVariable(Variables.varDDsR(flow, r3));
		verifyRouting.deactivateVariable(Variables.varDDsR(flow, r4));
		verifyRouting.deactivateVariable(Variables.varDDsR(flow, r5));
		verifyRouting.deactivateVariable(Variables.varDDsR(flow, r6));
		verifyRouting.deactivateVariable(Variables.varDDdR(flow, r1));
		verifyRouting.deactivateVariable(Variables.varDDdR(flow, r2));
		verifyRouting.deactivateVariable(Variables.varDDdR(flow, r3));
		verifyRouting.deactivateVariable(Variables.varDDdR(flow, r4));
		verifyRouting.deactivateVariable(Variables.varDDdR(flow, r5));
		verifyRouting.deactivateVariable(Variables.varDDdR(flow, r6));
		verifyRouting.deactivateVariable(ddlrr8_b);
		verifyRouting.deactivateVariable(Variables.varDDLRR(flow, l0, r1, r0));
		verifyRouting.deactivateVariable(Variables.varDDLRR(flow, l1, r2, r1));
		verifyRouting.deactivateVariable(Variables.varDDLRR(flow, l2, r3, r1));
		verifyRouting.deactivateVariable(Variables.varDDLRR(flow, l3, r4, r2));
		verifyRouting.deactivateVariable(Variables.varDDLRR(flow, l4, r5, r3));
		verifyRouting.deactivateVariable(Variables.varDDLRR(flow, l5, r6, r4));
		verifyRouting.deactivateVariable(Variables.varDDLRR(flow, l6, r6, r5));
		verifyRouting.deactivateVariable(Variables.varDDLRR(flow, l7, r7, r6));
		verifyRouting.deactivateVariable(ddlrr8_a);

		verifyRouting.verifyVariableActivated(ddlrr0);
		verifyRouting.verifyVariableActivated(ddlrr7);
		verifyRouting.verifyVariableActivated(Variables.varDDR(flow, r2));
		verifyRouting.verifyVariableActivated(Variables.varDDR(flow, r3));
		verifyRouting.verifyVariableActivated(ddlrr3);
		verifyRouting.verifyVariableActivated(ddlrr4);
		verifyRouting.verifyVariableActivated(ddlrr5);
		verifyRouting.verifyVariableActivated(Variables.varDDR(flow, r5));
		verifyRouting.verifyVariableActivated(ddlrr6);
	}
}
