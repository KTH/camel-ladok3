package se.kth.infosys.ladok3.utdata;

import se.ladok.schemas.studiedeltagande.UtdataResultatrad;

public class StudieaktivitetOchFinansiering {

  private String uid;

  private String studentPersonnummer;
  private String studentPersonnamn;

  private String kurspaketeringKod;
  private String kurspaketeringBenamning;
  private String kurspaketeringOmfattning;

  private String organisationsenhetKod;
  private String organisationsenhetBenamning;

  private String kurspaketeringsTillfalleKod;
  private String kurspaketeringsTillfallePeriod;

  private String studieaktivitetPeriod;
  private String studieaktivitetProcent;

  private String studiefinansieringProcent;
  private String studiefinansieringKod;
  private String studiefinansieringBenamning;

  public StudieaktivitetOchFinansiering(UtdataResultatrad next) {
    //uid = next.getMetadata().getEntry().get(0).getValue();
    studentPersonnummer = next.getVarden().get(0);
    studentPersonnamn = next.getVarden().get(1);
    kurspaketeringKod = next.getVarden().get(2);
    kurspaketeringBenamning = next.getVarden().get(3);
    kurspaketeringOmfattning = next.getVarden().get(4);
    organisationsenhetKod = next.getVarden().get(5);
    organisationsenhetBenamning = next.getVarden().get(6);
    kurspaketeringsTillfalleKod = next.getVarden().get(7);
    kurspaketeringsTillfallePeriod = next.getVarden().get(8);
    studieaktivitetPeriod = next.getVarden().get(9);
    studieaktivitetProcent = next.getVarden().get(10);
    studiefinansieringProcent = next.getVarden().get(11);
    studiefinansieringKod = next.getVarden().size() > 12 ? next.getVarden().get(12) : "";
    studiefinansieringBenamning = next.getVarden().size() > 12 ? next.getVarden().get(13) : "";
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getStudentPersonnummer() {
    return studentPersonnummer;
  }

  public void setStudentPersonnummer(String studentPersonnummer) {
    this.studentPersonnummer = studentPersonnummer;
  }

  public String getStudentPersonnamn() {
    return studentPersonnamn;
  }

  public void setStudentPersonnamn(String studentPersonnamn) {
    this.studentPersonnamn = studentPersonnamn;
  }

  public String getKurspaketeringKod() {
    return kurspaketeringKod;
  }

  public void setKurspaketeringKod(String kurspaketeringKod) {
    this.kurspaketeringKod = kurspaketeringKod;
  }

  public String getKurspaketeringBenamning() {
    return kurspaketeringBenamning;
  }

  public void setKurspaketeringBenamning(String kurspaketeringBenamning) {
    this.kurspaketeringBenamning = kurspaketeringBenamning;
  }

  public String getKurspaketeringOmfattning() {
    return kurspaketeringOmfattning;
  }

  public void setKurspaketeringOmfattning(String kurspaketeringOmfattning) {
    this.kurspaketeringOmfattning = kurspaketeringOmfattning;
  }

  public String getOrganisationsenhetKod() {
    return organisationsenhetKod;
  }

  public void setOrganisationsenhetKod(String organisationsenhetKod) {
    this.organisationsenhetKod = organisationsenhetKod;
  }

  public String getOrganisationsenhetBenamning() {
    return organisationsenhetBenamning;
  }

  public void setOrganisationsenhetBenamning(String organisationsenhetBenamning) {
    this.organisationsenhetBenamning = organisationsenhetBenamning;
  }

  public String getKurspaketeringsTillfalleKod() {
    return kurspaketeringsTillfalleKod;
  }

  public void setKurspaketeringsTillfalleKod(String kurspaketeringsTillfalleKod) {
    this.kurspaketeringsTillfalleKod = kurspaketeringsTillfalleKod;
  }

  public String getKurspaketeringsTillfallePeriod() {
    return kurspaketeringsTillfallePeriod;
  }

  public void setKurspaketeringsTillfallePeriod(String kurspaketeringsTillfallePeriod) {
    this.kurspaketeringsTillfallePeriod = kurspaketeringsTillfallePeriod;
  }

  public String getStudieaktivitetPeriod() {
    return studieaktivitetPeriod;
  }

  public void setStudieaktivitetPeriod(String studieaktivitetPeriod) {
    this.studieaktivitetPeriod = studieaktivitetPeriod;
  }

  public String getStudieaktivitetProcent() {
    return studieaktivitetProcent;
  }

  public void setStudieaktivitetProcent(String studieaktivitetProcent) {
    this.studieaktivitetProcent = studieaktivitetProcent;
  }

  public String getStudiefinansieringProcent() {
    return studiefinansieringProcent;
  }

  public void setStudiefinansieringProcent(String studiefinansieringProcent) {
    this.studiefinansieringProcent = studiefinansieringProcent;
  }

  public String getStudiefinansieringKod() {
    return studiefinansieringKod;
  }

  public void setStudiefinansieringKod(String studiefinansieringKod) {
    this.studiefinansieringKod = studiefinansieringKod;
  }

  public String getStudiefinansieringBenamning() {
    return studiefinansieringBenamning;
  }

  public void setStudiefinansieringBenamning(String studiefinansieringBenamning) {
    this.studiefinansieringBenamning = studiefinansieringBenamning;
  }
}