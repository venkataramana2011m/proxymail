package proxymail.core.services.impl;

import org.osgi.service.component.annotations.Component;
import static proxymail.core.constants.AppConstants.URL;
import proxymail.core.services.ReadJsonService;
import proxymail.core.utils.Network;

@Component(immediate = true, service = ReadJsonService.class)
public class ReadJsonDataImpl implements ReadJsonService {

	@Override
	public String getData() {
		// TODO Auto-generated method stub
		String response = Network.readJson(URL);

		return response;
	}

}
