package scanette;

import nz.ac.waikato.modeljunit.Action;
import nz.ac.waikato.modeljunit.FsmModel;

public class SCANETTE1 implements FsmModel {

    enum S {
        ATTENTE,
        COURSES,
        RELECTURE,
        ATT_CAISSIER,
        SESSION_CAISSIER,
        ATT_PAIEMENT,
        RELECTURE_ECHEC,
        FIN
    }

    private S state = S.ATTENTE;

    private boolean sessionOuverte = false;

    private int qteScanee = 0;
    private int qteConnue = 0;
    private boolean refsInconnues = false;
    private boolean abandon = false;

    private int qteCaisse = 0;

    @Override
    public Object getState() {
        return state;
    }

    @Override
    public void reset(boolean b) {
        state = S.ATTENTE;
        sessionOuverte = false;
        qteScanee = 0;
        qteConnue = 0;
        refsInconnues = false;
        qteCaisse = 0;
        abandon = false;
    }

    @Action
    public void demarrerCoursesOk() {
        state = S.COURSES;
    }

    public boolean demarrerCoursesOkGuard() {
        return state == S.ATTENTE;
    }


    @Action
    public void scannerConnu() {
        qteConnue++;
        state = S.COURSES;
    }

    public boolean scannerConnuGuard() {
        return state == S.COURSES;
    }

    @Action
    public void scannerInconnu() {
        refsInconnues = true;
        state = S.COURSES;
    }

    public boolean scannerInconnuGuard() {
        return state == S.COURSES;
    }

    @Action
    public void retirerConnuOk() {
        if (qteConnue > 0) qteConnue--;
        state = S.COURSES;
    }

    public boolean retirerConnuOkGuard() {
        return state == S.COURSES && qteConnue > 0;
    }

    @Action
    public void retirerConnuKo() {
        state = S.COURSES;
    }

    public boolean retirerConnuKoGuard() {
        return state == S.COURSES && qteConnue == 0;
    }

    @Action
    public void envoyerCaisseDemandeRelecture() {
        state = S.RELECTURE;
    }

    public boolean envoyerCaisseDemandeRelectureGuard() {
        return state == S.COURSES && qteConnue > 0 && !refsInconnues;
    }

    @Action
    public void suspectOuInconnu() {
        state = S.ATT_CAISSIER;
        qteCaisse = qteConnue;
    }

    public boolean suspectOuInconnuGuard() {
        return state == S.COURSES && (qteConnue == 0 || refsInconnues);
    }

    @Action
    public void envoyerCaisseVersPaiement() {
        state = S.ATT_PAIEMENT;
        qteCaisse = qteConnue;
    }

    public boolean envoyerCaisseVersPaiementGuard() {
        return state == S.COURSES && qteConnue > 0 && !refsInconnues;
    }

    @Action
    public void relectureScanAttendu() {
        state = S.RELECTURE;
        qteScanee++;
    }

    public boolean relectureScanAttenduGuard() {
        return state == S.RELECTURE && qteConnue != qteScanee  ;
    }

    @Action
    public void relectureEchec() {
        sessionOuverte = false;
        state = S.ATTENTE;
        qteConnue = 0;
        refsInconnues = false;
        qteCaisse = 0;
        qteScanee = 0;
    }

    public boolean relectureEchecGuard() {
        return state == S.RELECTURE;
    }

    @Action
    public void relectureOkTerminee() {
        state = S.ATT_PAIEMENT;
        qteCaisse = qteConnue;
    }

    public boolean relectureOkTermineeGuard() {
        return state == S.RELECTURE && qteScanee == qteConnue ;
    }

    @Action
    public void ouvrirSessionOk() {
        sessionOuverte = true;
        state = S.SESSION_CAISSIER;
    }

    public boolean ouvrirSessionOkGuard() {
        return !sessionOuverte && (state == S.ATT_CAISSIER || state == S.ATT_PAIEMENT);
    }

    @Action
    public void ouvrirSessionKo() {
        state = S.SESSION_CAISSIER;
    }

    public boolean ouvrirSessionKoGuard() {
        return !sessionOuverte && (state == S.SESSION_CAISSIER );
    }

    @Action
    public void caissierAjouteConnu() {
        qteCaisse++;
        state = S.SESSION_CAISSIER;
    }

    public boolean caissierAjouteConnuGuard() {
        return state == S.SESSION_CAISSIER && sessionOuverte;
    }

    @Action
    public void caissierRetireConnuOk() {
        if (qteCaisse > 0) qteCaisse--;
        state = S.SESSION_CAISSIER;
    }

    public boolean caissierRetireConnuOkGuard() {
        return state == S.SESSION_CAISSIER && sessionOuverte && qteCaisse > 0;
    }

    @Action
    public void caissierRetireConnuKo() {
        state = S.SESSION_CAISSIER;
    }

    public boolean caissierRetireConnuKoGuard() {
        return state == S.SESSION_CAISSIER && sessionOuverte && qteCaisse == 0;
    }

    @Action
    public void fermerSessionOkVersPaiement() {
        sessionOuverte = false;
        state = S.ATT_PAIEMENT;
    }

    public boolean fermerSessionOkVersPaiementGuard() {
        return state == S.SESSION_CAISSIER && sessionOuverte && qteCaisse > 0;
    }

    @Action
    public void fermerSuspect() {
        sessionOuverte = false;
        state = S.ATTENTE;
        qteConnue = 0;
        refsInconnues = false;
        qteCaisse = 0;
        qteScanee = 0;
    }

    public boolean fermerSuspectGuard() {
        return state == S.SESSION_CAISSIER && sessionOuverte && qteCaisse == 0;
    }

    @Action
    public void fermerSessionKo() {
        state = S.ATT_CAISSIER;
    }

    public boolean fermerSessionKoGuard() {
        return state == S.ATT_CAISSIER;
    }

    @Action
    public void payerOk() {
        state = S.FIN;
    }

    public boolean payerOkGuard() {
        return state == S.ATT_PAIEMENT && qteCaisse > 0;
    }

    @Action
    public void payerKo() {
        state = S.ATT_PAIEMENT;
    }

    public boolean payerKoGuard() {
        return state == S.ATT_PAIEMENT && qteCaisse > 0;
    }

    @Action
    public void abandonner() {
        sessionOuverte = false;
        state = S.ATTENTE;
        qteConnue = 0;
        refsInconnues = false;
        qteCaisse = 0;
        qteScanee = 0;
    }

    public boolean abandonnerGuard() {
        return state != S.FIN && !abandon;
    }

    @Action
    public void terminer() {
        sessionOuverte = false;
        state = S.ATTENTE;
        qteConnue = 0;
        refsInconnues = false;
        qteCaisse = 0;
        qteScanee = 0;
    }

    public boolean terminerGuard() {
        return state == S.FIN;
    }
}
