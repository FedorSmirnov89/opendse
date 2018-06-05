package net.sf.opendse.encoding.module;

import org.opt4j.core.config.Icons;
import org.opt4j.core.config.annotations.Icon;
import org.opt4j.core.config.annotations.Parent;
import org.opt4j.core.start.Opt4JModule;

import net.sf.opendse.encoding.SpecificationPreprocessor;
import net.sf.opendse.encoding.preprocessing.ProxySearch;
import net.sf.opendse.optimization.DesignSpaceExplorationModule;

/**
 * The {@link ProxyRoutingModule} binds the classes necessary for a proxy-based
 * routing encoding.
 * 
 * @author Fedor Smirnov
 *
 */
@Parent(DesignSpaceExplorationModule.class)
@Icon(Icons.PROBLEM)
public class ProxyRoutingModule extends Opt4JModule {

	@Override
	protected void config() {
		bind(SpecificationPreprocessor.class).to(ProxySearch.class);
	}
}
