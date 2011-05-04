package intact.solution.psicquic.registry;

import org.hupo.psi.mi.psicquic.registry.ServiceType;
import org.hupo.psi.mi.psicquic.registry.client.registry.DefaultPsicquicRegistryClient;
import org.hupo.psi.mi.psicquic.registry.client.registry.PsicquicRegistryClient;

import java.util.List;

/**
 * List the PSICQUIC Services by its tags, using the Registry.
 */
public class ListingServicesByTags {

    public static void main(String[] args) throws Exception {
        // instantiate the registry client
        PsicquicRegistryClient registryClient = new DefaultPsicquicRegistryClient();

        // Exercise: List services with the tag 'imex curation'
        final List<ServiceType> services = registryClient.listServicesByTags("imex curation");

        for (ServiceType service : services) {
            System.out.println(service.getName()+" - "+service.getCount()+" - "+service.getRestExample());
        }

    }

}
