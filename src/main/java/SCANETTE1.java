import nz.ac.waikato.modeljunit.Action;
import nz.ac.waikato.modeljunit.FsmModel;
import vbm.projet.ArticleDB;

public class SCANETTE1 implements FsmModel {



    private class Panier{
        ArticleDB ArticleDBvide = new ArticleDB();
    }
    @Override
    public Object getState() {
        return state;
    }

    @Override
    public void reset(boolean b) {
        state = "En attente";
    }
    String state = "En attente";

    @Action
    public void AuthentificationOK(){
        Panier a = new Panier();
        if(a.ArticleDBvide.getTailleDB() == 0) {
            state = "Authentifie";
        }
    }

    public boolean AuthentificationOKGuard(){
        return state.equals("En attente");
    }

    @Action
    public void AuthentificationKO(){
        Panier a = new Panier();
        if(a.ArticleDBvide.getTailleDB() != 0) {
            state = "En attente";
        }
    }

    public boolean AuthentificationKOGuard(){
        return state.equals("En attente");
    }

    @Action
    public void AjouterArticleOK(){
        state = "Authentifie";
    }

    public boolean AjouterArticleOKGuard(){
        return state.equals("Authentifie");
    }

    @Action
    public void SupprimerArticleOK(){
        state = "Authentifie";
    }

    public boolean SupprimerArticleOKGuard(){
        return state.equals("Authentifie");
    }

    @Action
    public void AjouterArticleKO(){
        state = "Authentifie";
    }

    public boolean AjouterArticleKOGuard(){
        return state.equals("Authentifie");
    }

    @Action
    public void SupprimerArticleKO(){
        state = "Authentifie";
    }

    public boolean SupprimerArticleKOGuard(){
        return state.equals("Authentifie");
    }

    @Action
    public void TransfererCaisse(){
        state = "Caisse";
    }

    public boolean TransfererCaisseGuard(){
        return state.equals("Authentifie");
    }

    @Action
    public void ArticlesPasDefaillantPasAleatoire(){state = "Paiement";}

    public boolean ArticlesPasDefaillantPasAleatoireGuard(){
        return state.equals("Caisse");
    }

    @Action
    public void ArticlesDefaillant(){state = "Verification article";}

    public boolean ArticlesDefaillantGuard(){
        return state.equals("Caisse");
    }

    @Action
    public void VerifAleatoire(){state = "Validation";}

    public boolean VerifAleatoireGuard(){
        return state.equals("Verification article");
    }

    @Action
    public void PasVerifAleatoire(){state = "Paiement";}

    public boolean PasVerifAleatoireGuard(){
        return state.equals("Verification article");
    }

    @Action
    public void ValidationOk(){state = "Paiement";}

    public boolean ValidationOkGuard(){
        return state.equals("Validation");
    }

    @Action
    public void ValidationKO(){state = "Reverification totale";}

    public boolean ValidationKOGuard(){
        return state.equals("Validation");
    }

    @Action
    public void CB(){state = "CB";}

    public boolean CBGuard(){
        return state.equals("Paiement");
    }

    @Action
    public void Espece(){state = "Espece";}

    public boolean EspeceGuard(){
        return state.equals("Paiement");
    }

    @Action
    public void ReverificationOk(){state = "Paiement";}

    public boolean ReverificationOkGuard(){
        return state.equals("Reverification totale");
    }

    @Action
    public void ReverificationKO(){state = "Caisse";}

    public boolean ReverificationKOGuard(){
        return state.equals("Reverification totale");
    }

    @Action
    public void OuvertureSession(){state = "Validation";}

    public boolean OuvertureSessionGuard(){
        return state.equals("Validation");
    }

    @Action
    public void FermetureSession(){state = "Validation";}

    public boolean FermetureSessionGuard(){
        return state.equals("Validation");
    }

    @Action
    public void FermetureSessionKO(){state = "Caisse";}

    public boolean FermetureSessionKOGuard(){
        return state.equals("Validation");
    }
}
