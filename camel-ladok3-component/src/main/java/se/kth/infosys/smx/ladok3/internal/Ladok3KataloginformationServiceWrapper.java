package se.kth.infosys.smx.ladok3.internal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.SSLContext;
import org.apache.camel.CamelExchangeException;
import org.apache.camel.Exchange;
import org.apache.camel.support.ExchangeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.infosys.ladok3.KataloginformationService;
import se.kth.infosys.ladok3.KataloginformationServiceImpl;
import se.kth.infosys.smx.ladok3.Ladok3Message;
import se.ladok.schemas.kataloginformation.Anvandare;
import se.ladok.schemas.kataloginformation.Anvandarinformation;
import se.ladok.schemas.kataloginformation.OrganisationLista;

public class Ladok3KataloginformationServiceWrapper implements Ladok3ServiceWrapper {
  private static final Logger log = LoggerFactory.getLogger(Ladok3KataloginformationServiceWrapper.class);
  private static final Pattern URL_PATTERN = Pattern.compile(
          "^/kataloginformation(/(?<operation>" +
                  "anvandare|" +
                  "createAnvandare|" +
                  "updateAnvandare|" +
                  "filtrera|" +
                  "anvandarinformation|" +
                  "createAnvandarinformation|" +
                  "updateAnvandarinformation|" +
                  "organisation" +
                  "))+.*");

  private final KataloginformationService service;
  private final String pathOperation;

  public Ladok3KataloginformationServiceWrapper(String host, String path, SSLContext context) throws Exception {
    this.service = new KataloginformationServiceImpl(host, context);
    Matcher matcher = URL_PATTERN.matcher(path);
    if (matcher.matches()) {
        pathOperation = matcher.group("operation").toLowerCase();
    } else {
        pathOperation = null;
    }
  }

  public void doExchange(Exchange exchange) throws Exception {
    final String operation = currentOperation(exchange);

    switch (operation) {
      case "anvandare":
          handleAnvandareRequest(exchange);
          break;
      case "createAnvandare":
          handleCreateAnvandareRequest(exchange);
          break;
      case "updateAnvandare":
          handleUpdateAnvandareRequest(exchange);
          break;
      case "filtrera":
          handleFiltreraRequest(exchange);
          break;
      case "anvandarinformation":
          handleAnvandarinformationRequest(exchange);
          break;
      case "createAnvandarinformation":
          handleCreateAnvandarinformationRequest(exchange);
          break;
      case "updateAnvandarinformation":
          handleUpdateAnvandarinformationRequest(exchange);
          break;
      case "organisation":
        handleGetOrganisationRequest(exchange);
        break;
      default:
          throw new CamelExchangeException("Unupported operation: %s" + operation, exchange);
      }
  }

  private void handleAnvandareRequest(final Exchange exchange) throws Exception {
    String uid = exchange.getIn().getHeader(Ladok3Message.Header.KeyValue, String.class);

    if (uid == null || uid.isEmpty()) {
      Anvandare anvandare = exchange.getIn().getMandatoryBody(Anvandare.class);
      uid = anvandare.getUid();
    }

    log.debug("Getting anvandare with uid: {}", uid);
    final Anvandare fromLadok = service.anvandare(uid);
    exchange.getIn().setBody(fromLadok);
  }

  private void handleCreateAnvandareRequest(final Exchange exchange) throws Exception {
    final Anvandare anvandare = exchange.getIn().getMandatoryBody(Anvandare.class);

    log.debug("Creating anvandare with username: {}", anvandare.getAnvandarnamn());
    final Anvandare fromLadok = service.createAnvandare(anvandare);
    exchange.getIn().setBody(fromLadok);
  }

  private void handleUpdateAnvandareRequest(final Exchange exchange) throws Exception {
    final Anvandare anvandare = exchange.getIn().getMandatoryBody(Anvandare.class);

    log.debug("Updating anvandare with uid: {}", anvandare.getUid());
    final Anvandare fromLadok = service.updateAnvandare(anvandare);
    exchange.getIn().setBody(fromLadok);
  }

  private void handleFiltreraRequest(final Exchange exchange) {
    @SuppressWarnings("unchecked")
    final HashMap<String, Object> params = exchange.getIn().getHeader(
        Ladok3Message.Header.Params, new HashMap<String, Object>(), HashMap.class);

    log.debug("Getting anvandare for filtrera request with params: {}", params);
    final Iterator<Anvandare> fromLadok = service.anvandareFiltrerade(params).getAnvandare().iterator();
    exchange.getIn().setBody(fromLadok);
  }

  private void handleAnvandarinformationRequest(final Exchange exchange) throws Exception {
    String uid = exchange.getIn().getHeader(Ladok3Message.Header.KeyValue, String.class);

    if (uid == null || uid.isEmpty()) {
      Anvandare anvandare = exchange.getIn().getMandatoryBody(Anvandare.class);
      uid = anvandare.getUid();
    }

    log.debug("Getting anvandareinformation for anvandare with uid: {}", uid);
    final Anvandarinformation fromLadok = service.anvandarinformation(uid);
    exchange.getIn().setBody(fromLadok);
  }

  private void handleCreateAnvandarinformationRequest(final Exchange exchange) throws Exception {
    final Anvandarinformation anvandarinformation = exchange.getIn().getMandatoryBody(Anvandarinformation.class);

    log.debug("Creating anvandarinformation for user with uid: {}", anvandarinformation.getAnvandareUID());
    final Anvandarinformation fromLadok = service.updateAnvandarinformation(anvandarinformation);
    exchange.getIn().setBody(fromLadok);
  }

  private void handleUpdateAnvandarinformationRequest(final Exchange exchange) throws Exception {
    final Anvandarinformation anvandarinformation = exchange.getIn().getMandatoryBody(Anvandarinformation.class);

    log.debug("Updating anvandarinformation for user with uid: {}", anvandarinformation.getAnvandareUID());
    final Anvandarinformation fromLadok = service.updateAnvandarinformation(anvandarinformation);
    exchange.getIn().setBody(fromLadok);
  }

  private void handleGetOrganisationRequest(final Exchange exchange) throws Exception {

    log.debug("Getting organisation information");
    final OrganisationLista fromLadok = service.organisationLista();
    exchange.getIn().setBody(fromLadok);
  }
  private String currentOperation(final Exchange exchange) throws Exception {
    if (pathOperation != null && ! pathOperation.isEmpty()) {
      return pathOperation;
    }

    return ExchangeHelper.getMandatoryHeader(exchange, Ladok3Message.Header.Operation, String.class);
    }
}
