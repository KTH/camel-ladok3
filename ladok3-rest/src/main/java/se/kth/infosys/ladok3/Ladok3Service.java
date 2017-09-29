package se.kth.infosys.ladok3;

import se.ladok.schemas.dap.ServiceIndex;

/**
 * Base interface defining common parts of other interfaces.
 */
public interface Ladok3Service {
    /**
     * Get the service index for the service.
     * 
     * While the Ladok project says we should use this index, why we should and for 
     * what purpose really escapes me, so I'm stalling it for now. - fjo 20161018
     * 
     * @return The service index
     */
    public ServiceIndex serviceIndex();
}
