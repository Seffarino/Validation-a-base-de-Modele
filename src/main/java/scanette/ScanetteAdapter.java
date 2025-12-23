package scanette;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Iterator;

import vbm.projet.*;

public class ScanetteAdapter {

    private ArticleDB db;
    private Scanette scanette;
    private Caisse caisse;

    private long eanConnu  = 3046920010856L;
    private long eanInconnu = 9999999999999L;
    private long eanNonPresent = 8718309259938L;
    public void setup() {
        db = new ArticleDB();
        try {
            db.init(new File("src/main/resources/csv/produits.csv"));
        } catch (FileFormatException e) {
            throw new RuntimeException(e);
        }

        scanette = new Scanette(db);
        caisse = new Caisse(db);

        caisse.changerProbabiliteRelecture(0.0);

    }

    public void teardown() {
        scanette.abandon();
        caisse.abandon();
    }

    public void demarrerCoursesOk() {
        assertEquals(0, scanette.debloquer());
    }


    public void scannerConnu() {
        assertEquals(0, scanette.scanner(eanConnu));
    }

    public void scannerInconnu() {
        assertEquals(-2, scanette.scanner(eanInconnu));
    }

    public void retirerConnuOk() {
        assertEquals(0, scanette.supprimer(eanConnu));
    }

    public void retirerConnuKo() {
        assertEquals(-2, scanette.supprimer(eanConnu));
    }

    public void envoyerCaisseVersPaiement() {
        caisse.changerProbabiliteRelecture(0.0);
        assertEquals(0, scanette.transmission(caisse));
    }

    public void suspectOuInconnu() {
        caisse.changerProbabiliteRelecture(0.0);
        assertEquals(0, scanette.transmission(caisse));
    }

    public void envoyerCaisseDemandeRelecture() {
        caisse.changerProbabiliteRelecture(1.0);
        assertEquals(1, scanette.transmission(caisse));
    }

    public void relectureScanAttendu() {
        assertEquals(0, scanette.scanner(eanConnu));
    }

    public void relectureEchec() {
        scanette.abandon();
    }

    public void relectureOkTerminee() {
        caisse.changerProbabiliteRelecture(0.0);
        assertEquals(0, scanette.transmission(caisse));
    }

    public void ouvrirSessionOk() {
        assertEquals(0, caisse.ouvrirSession());
    }

    public void ouvrirSessionKo() {
        assertEquals(-1, caisse.ouvrirSession());
    }

    public void caissierAjouteConnu() {
        assertEquals(0, caisse.scanner(eanConnu));
    }

    public void caissierRetireConnuOk() {
        assertEquals(0, caisse.supprimer(eanConnu));
    }

    public void caissierRetireConnuKo() {
        assertEquals(-2, caisse.supprimer(eanConnu));
    }

    public void fermerSessionOkVersPaiement() {
        assertEquals(0, caisse.fermerSession());
    }

    public void fermerSuspect() {
        assertEquals(0, caisse.fermerSession());
    }

    public void fermerSessionKo() {
        assertEquals(-1, caisse.fermerSession());
    }

    public void payerOk() {
        assertTrue(caisse.payer(1000.0) >= 0);
    }

    public void payerKo() {
        assertTrue(caisse.payer(0.0) < 0);
    }

    public void abandonner() {
        scanette.abandon();
        caisse.abandon();
    }

    public void terminer() {
        scanette.abandon();
        caisse.abandon();
    }
}
