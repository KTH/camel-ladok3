package se.kth.infosys.ladok3;

import se.ladok.schemas.dap.ServiceIndex;

public interface InformationService {
    /**
     * Get the service index for the service.
     * 
     * NOTE: This could probably have been made generic in the base class, but
     * the "ACCEPT" types are different for each service. The idea to keep an abstract
     * definition here is to use it in order to provide a generic structure to make
     * lookups into this information. However, while the Ladok project says we should
     * use this index, why we should and for what purpose really escapes me, so I'm 
     * stalling it for now. - fjo 20161018
     * 
     * @return The service index
     */
    public ServiceIndex serviceIndex();
}
