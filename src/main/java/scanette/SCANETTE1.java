package scanette;

import nz.ac.waikato.modeljunit.Action;
import nz.ac.waikato.modeljunit.FsmModel;
import vbm.projet.ArticleDB;
import vbm.projet.Caisse;
import vbm.projet.Scanette;

import java.io.File;

public class SCANETTE1 implements FsmModel {

    String state = "En attente";


    private boolean sessionOuverte = false;


    private ArticleDB db;
    private Scanette scanette;
    private Caisse caisse;

    private static final long EAN_EXISTANT = 5410188006711L;
    private static final long EAN_INEXISTANT = 1111111111111L;

    public SCANETTE1() {
        init();
    }

    private void init() {
        db = new ArticleDB();
        try {
            db.init(new File("src/main/resources/csv/produits.csv"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        scanette = new Scanette(db);
        caisse = new Caisse(db);
    }

    @Override
    public Object getState() { return state; }

    @Override
    public void reset(boolean b) {
        state = "En attente";
        sessionOuverte = false;
        init();
    }



    @Action
    public void AuthentificationOK() {
        int ret = scanette.debloquer();

        if (ret == 0) {      // succès
            state = "Authentifie";
        }
    }

    public boolean AuthentificationOKGuard() {
        return state.equals("En attente");
    }

    @Action
    public void AuthentificationKO() {
        state = "En attente";
    }

    public boolean AuthentificationKOGuard() {
        return state.equals("En attente");
    }
    @Action
    public void AjouterArticleOK() {
        int ret = scanette.scanner(EAN_EXISTANT);
        if (ret == 0) {
            state = "Authentifie";
        }
    }

    public boolean AjouterArticleOKGuard() {
        return state.equals("Authentifie")
                && db.getArticle(EAN_EXISTANT) != null;
    }

    @Action
    public void AjouterArticleKO() {
        int ret = scanette.scanner(EAN_INEXISTANT);
        if (ret == 0) {
            state = "Authentifie";
        }
    }

    public boolean AjouterArticleKOGuard() {
        return state.equals("Authentifie")
                && db.getArticle(EAN_INEXISTANT) == null;
    }



    @Action
    public void SupprimerArticleOK() {
        int ret = scanette.supprimer(EAN_EXISTANT);
        if (ret == 0) {
            state = "Authentifie";
        }
    }

    public boolean SupprimerArticleOKGuard() {
        return state.equals("Authentifie")
                && db.getArticle(EAN_EXISTANT) != null
                && scanette.quantite(EAN_EXISTANT) > 0;
    }

    @Action
    public void SupprimerArticleKO() {
        int ret = scanette.supprimer(EAN_EXISTANT);
        if (ret == 0) {
            state = "Authentifie";
        }
    }

    public boolean SupprimerArticleKOGuard() {
        return state.equals("Authentifie")
                && db.getArticle(EAN_EXISTANT) != null
                && scanette.quantite(EAN_EXISTANT) == 0;
    }


    @Action
    public void TransfererCaisse() {
        int ret = scanette.transmission(caisse);
        if (ret == 1) {
            state = "Verification article";
        }
        else if (ret == 0) {
            state = "Caisse";
        }
    }
    public boolean TransfererCaisseGuard() {
        return state.equals("Authentifie");
    }


    @Action
    public void ArticlesPasDefaillantPasAleatoire() {
        state = "Paiement";
    }
    public boolean ArticlesPasDefaillantPasAleatoireGuard() {
        return state.equals("Caisse")
                && !scanette.getArticles().isEmpty()
                && scanette.getReferencesInconnues().isEmpty();
    }

    @Action
    public void ArticlesDefaillant() {
        state = "AttenteCaissier";
    }

    public boolean ArticlesDefaillantGuard() {
        return state.equals("Caisse")
                && (scanette.getArticles().isEmpty()
                || !scanette.getReferencesInconnues().isEmpty());
    }


    @Action
    public void VerifAleatoire(){ state = "Validation"; }

    public boolean VerifAleatoireGuard(){ return state.equals("Verification article"); }

    @Action
    public void PasVerifAleatoire(){ state = "Paiement"; }

    public boolean PasVerifAleatoireGuard(){ return state.equals("Verification article"); }

    @Action
    public void ValidationOk() {
        state = "Paiement";
    }

    public boolean ValidationOkGuard() {
        return state.equals("Validation")
                && scanette.relectureEffectuee();
    }

    @Action
    public void ValidationKO() {
        state = "Reverification totale";
    }
    public boolean ValidationKOGuard() {
        return state.equals("Validation")
                && !scanette.relectureEffectuee();
    }

    @Action
    public void CB(){ state = "CB"; }

    public boolean CBGuard(){ return state.equals("Paiement"); }

    @Action
    public void Espece(){ state = "Espece"; }

    public boolean EspeceGuard(){ return state.equals("Paiement"); }

    @Action
    public void ReverificationOk(){ state = "Paiement"; }

    public boolean ReverificationOkGuard(){ return state.equals("Reverification totale"); }

    @Action
    public void ReverificationKO(){ state = "Caisse"; }

    public boolean ReverificationKOGuard(){ return state.equals("Reverification totale"); }

    @Action
    public void OuvertureSession() {
        int ret = caisse.ouvrirSession();
        if (ret == 0) {
            sessionOuverte = true;
            state = "Validation";
        }
    }

    public boolean OuvertureSessionGuard() {
        return !sessionOuverte &&
                (state.equals("Paiement") || state.equals("AttenteCaissier"));
    }


    @Action
    public void FermetureSession() {
        int ret = caisse.fermerSession();
        if (ret == 0) {
            sessionOuverte = false;
            state = "Paiement";
        }
    }

    public boolean FermetureSessionGuard() {
        return state.equals("Validation");
    }
    @Action
    public void FermetureSessionKO() {
        state = "Caisse";
    }

    public boolean FermetureSessionKOGuard() {
        return state.equals("Validation");
    }

}
