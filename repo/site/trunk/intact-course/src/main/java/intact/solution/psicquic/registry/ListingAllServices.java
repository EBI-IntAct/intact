package intact.solution.psicquic.registry;

import org.hupo.psi.mi.psicquic.registry.ServiceType;
import org.hupo.psi.mi.psicquic.registry.client.registry.DefaultPsicquicRegistryClient;
import org.hupo.psi.mi.psicquic.registry.client.registry.PsicquicRegistryClient;

import java.util.List;

/**
 * List all the available Services using the PSICQUIC Registry.
 */
public class ListingAllServices {

    public static void main(String[] args) throws Exception {
        // instantiate the registry client
        PsicquicRegistryClient registryClient = new DefaultPsicquicRegistryClient();

        // Exercise: List the names of all the services in the registry
        final List<ServiceType> services = registryClient.listServices();

        for (ServiceType service : services) {
            System.out.println(service.getName());
        }

        System.out.println("-------------------------------------------------");

        // Exercise: Iterate through all the ACTIVE services listing its name, interaction count and REST Example URLs.
        final List<ServiceType> activeServices = registryClient.listActiveServices();

        for (ServiceType service : activeServices) {
            System.out.println(service.getName()+" - "+service.getCount()+" - "+service.getRestExample());
        }
    }

}
