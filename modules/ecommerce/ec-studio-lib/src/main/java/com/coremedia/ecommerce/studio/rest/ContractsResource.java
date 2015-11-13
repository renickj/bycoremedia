package com.coremedia.ecommerce.studio.rest;

import com.coremedia.ecommerce.studio.rest.model.Contracts;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.contract.Contract;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The resource handles the top level store node "Contracts".
 * It is not showed in the catalog tree but used to invalidate the list of available commerce contracts
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("livecontext/contracts/{siteId:[^/]+}/{workspaceId:[^/]+}")
public class ContractsResource extends AbstractCatalogResource<Contracts> {

  @Override
  public ContractsRepresentation getRepresentation() {
    ContractsRepresentation representation = new ContractsRepresentation();
    fillRepresentation(representation);
    return representation;
  }

  private void fillRepresentation(ContractsRepresentation representation) {
    try {
      Contracts contracts = getEntity();

      if (contracts == null) {
        LOG.error("Error loading contracts bean");
        throw new CatalogRestException(Response.Status.NOT_FOUND, CatalogRestErrorCodes.COULD_NOT_FIND_CATALOG_BEAN, "Could not load contracts bean");
      }

      representation.setId(contracts.getId());
      if (getConnection().getContractService() != null) {
        Collection<Contract> contractIdsForServiceUser = getConnection().getContractService().findContractIdsForServiceUser(getStoreContext());
        //filter default contract from contract list
        Collection<Contract> filteredContracts = new ArrayList<>();
        for (Contract contract : contractIdsForServiceUser) {
          if (!contract.isDefaultContract()){
            filteredContracts.add(contract);
          }
        }
        representation.setContracts(filteredContracts);
      }

    } catch (CommerceException ex) {
      CommerceStudioErrorHandler.handleCommerceException(ex);
    }
  }

  @Override
  protected Contracts doGetEntity() {
    return new Contracts(getStoreContext());
  }

  @Override
  public void setEntity(Contracts contracts) {
    setSiteId(contracts.getContext().getSiteId());
    setWorkspaceId(contracts.getContext().getWorkspaceId());
  }
}
