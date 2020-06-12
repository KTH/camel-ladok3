package se.kth.infosys.smx.ladok3.internal;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.infosys.ladok3.ResultatService;
import se.kth.infosys.ladok3.ResultatServiceImpl;
import se.kth.infosys.smx.ladok3.Ladok3Message;
import se.ladok.schemas.resultat.Aktivitetstillfalle;
import se.ladok.schemas.resultat.AktivitetstillfalleForStudentLista;
import se.ladok.schemas.resultat.SokresultatStudieresultatResultat;

public class Ladok3ResultatServiceWrapper implements Ladok3ServiceWrapper {
  private static final Logger log = LoggerFactory.getLogger(Ladok3StudentInformationServiceWrapper.class);
  private static final Pattern URL_PATTERN = Pattern.compile(
        "^/resultat(/(?<operation>"
        + "aktivitetstillfalle|"
        + "aktivitetstillfalle/student|" 
        + "studieresultat/rapportera/aktivitetstillfalle"
        + "))");

  private ResultatService service;
  private String pathOperation;

  public Ladok3ResultatServiceWrapper(String host, String path, SSLContext context) throws Exception {
    this.service = new ResultatServiceImpl(host, context);
    Matcher matcher = URL_PATTERN.matcher(path);
    if (matcher.matches()) {
        pathOperation = matcher.group("operation").toLowerCase();
    }
  }

  public void doExchange(Exchange exchange) throws Exception {
    final String operation = currentOperation(exchange);

    switch (operation) {
      case "aktivitetstillfalle":
        handleAktivitetsfillfalleRequest(exchange);
      break;
      case "aktivitetstillfalle/student":
        handleAktivitetstillfalleForStudent(exchange);
        break;
      case "studieresultat/rapportera/aktivitetstillfalle":
        handleSokresultatStudieresultatResultat(exchange);
        break;
      default:
        log.warn("Unsupported operation");
    }
  }

  private void handleAktivitetsfillfalleRequest(final Exchange exchange) throws Exception {
    String uid = exchange.getMessage().getHeader(Ladok3Message.Header.KeyValue, String.class);
    
    if (uid.isBlank()) {
      Aktivitetstillfalle akt = exchange.getMessage().getMandatoryBody(Aktivitetstillfalle.class);
      uid = akt.getUid();
    }

    Aktivitetstillfalle fromLadok = service.aktivitetstillfalle(uid);
    exchange.getMessage().setBody(fromLadok);
  }

  private void handleAktivitetstillfalleForStudent(final Exchange exchange) throws Exception {
    String uid = exchange.getMessage().getHeader(Ladok3Message.Header.KeyValue, String.class);

    AktivitetstillfalleForStudentLista fromLadok = service.aktivitetstillfalleForStudentLista(uid);
    exchange.getMessage().setBody(fromLadok);
  }

  private void handleSokresultatStudieresultatResultat(final Exchange exchange) throws Exception {
    String aktivitetstillfalleUID = exchange.getMessage().getHeader(Ladok3Message.Header.KeyValue, String.class);
    @SuppressWarnings("unchecked")
    HashMap<String, Object> params = exchange.getIn().getHeader(Ladok3Message.Header.Params, new HashMap<String, Object>(), HashMap.class);

    SokresultatStudieresultatResultat fromLadok = service.sokresultatStudieresultatResultat(aktivitetstillfalleUID, params);
    exchange.getMessage().setBody(fromLadok);
  }
  
  private String currentOperation(final Exchange exchange) throws Exception {
    if (pathOperation != null && ! pathOperation.isEmpty()) {
        return pathOperation;
    }

    String headerOperation = exchange.getIn().getHeader(Ladok3Message.Header.Operation, String.class);
    if (headerOperation != null && ! headerOperation.isEmpty()) {
        return headerOperation;
    }

    return "uid";
  }
}