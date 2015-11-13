package com.coremedia.ecommerce.studio.rest;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.contract.Contract;
import com.coremedia.livecontext.ecommerce.contract.ContractService;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

/**
 * A catalog {@link Contract} object as a RESTful resource.
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("livecontext/contract/{siteId:[^/]+}/{workspaceId:[^/]+}/{id:[^/]+}")
public class ContractResource extends AbstractCatalogResource<Contract> {

  @Override
  public ContractRepresentation getRepresentation() {
    ContractRepresentation representation = new ContractRepresentation();
    fillRepresentation(representation);
    return representation;
  }

  private void fillRepresentation(ContractRepresentation representation) {
    try {
      Contract entity = getEntity();

      if (entity == null) {
        LOG.error("Error loading contract bean");
        throw new CatalogRestException(Response.Status.NOT_FOUND, CatalogRestErrorCodes.COULD_NOT_FIND_CATALOG_BEAN, "Could not load contract bean");
      }

      representation.setId(entity.getId());
      representation.setName(entity.getName());
      representation.setExternalId(entity.getExternalId());
      representation.setExternalTechId(entity.getExternalTechId());

    } catch (CommerceException ex) {
      CommerceStudioErrorHandler.handleCommerceException(ex);
    }
  }

  @Override
  protected Contract doGetEntity() {
    ContractService contractService = getContractService();
    if (contractService != null) {
      //iterating all eligible contracts is a workaround since the ibm findContractById call does not consider the storeId.
      //therefor we make use of the eligible call since it does consider the storeId.
      Collection<Contract> contracts = contractService.findContractIdsForServiceUser(getStoreContext());
      for (Contract contract : contracts) {
        if (contract.getExternalId() != null && contract.getExternalId().equals(getId())) {
          return contractService.findContractById(getId());
        }
      }
    }
    return null;
  }

  @Override
  public void setEntity(Contract contract) {
    if (contract.getId() != null) {
      String extId = getExternalIdFromId(contract.getId());
      setId(extId);
    } else {
      setId(contract.getExternalId());
    }
    setSiteId(contract.getContext().getSiteId());
    setWorkspaceId(contract.getContext().getWorkspaceId());
  }

  public ContractService getContractService() {
    return Commerce.getCurrentConnection().getContractService();
  }


}
