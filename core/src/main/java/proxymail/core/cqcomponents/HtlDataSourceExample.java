package proxymail.core.cqcomponents;

import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.commons.collections.iterators.*;

import com.adobe.cq.sightly.WCMUsePojo;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;

public class HtlDataSourceExample extends WCMUsePojo {

	@Override
	public void activate() throws Exception {
		final ResourceResolver resolver = getResource().getResourceResolver();


		final Map<String, String> countries = new LinkedHashMap<String, String>();

		countries.put("in", "India");
		countries.put("us", "United States");
		countries.put("aus", "Australia");
		countries.put("pak", "Pakistan");
		countries.put("sri", "Srilanka");

		@SuppressWarnings("unchecked")
		DataSource ds = new SimpleDataSource(new TransformIterator(countries.keySet().iterator(), new Transformer() {

			@Override
			public Object transform(Object o) {
				String country = (String) o;


				ValueMap vm = new ValueMapDecorator(new HashMap<String, Object>());


				vm.put("value", country);
				vm.put("text", countries.get(country));

				return new ValueMapResource(resolver, new ResourceMetadata(), "nt:unstructured", vm);
			}
		}));

		this.getRequest().setAttribute(DataSource.class.getName(), ds);

	}
}