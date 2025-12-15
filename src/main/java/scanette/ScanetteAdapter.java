package scanette;
public class ScanetteAdapter {

    private SCANETTE1 model;

    public void setup() {
        model = new SCANETTE1();
        model.reset(true);
    }

    public void teardown() {
        // rien à faire
    }

    public void AuthentificationOK() { model.AuthentificationOK(); }
    public void AuthentificationKO() { model.AuthentificationKO(); }

    public void AjouterArticleOK() { model.AjouterArticleOK(); }
    public void AjouterArticleKO() { model.AjouterArticleKO(); }

    public void SupprimerArticleOK() { model.SupprimerArticleOK(); }
    public void SupprimerArticleKO() { model.SupprimerArticleKO(); }

    public void TransfererCaisse() { model.TransfererCaisse(); }

    public void ArticlesDefaillant() { model.ArticlesDefaillant(); }
    public void ArticlesPasDefaillantPasAleatoire() {
        model.ArticlesPasDefaillantPasAleatoire();
    }

    public void VerifAleatoire() { model.VerifAleatoire(); }
    public void PasVerifAleatoire() { model.PasVerifAleatoire(); }

    public void ValidationOk() { model.ValidationOk(); }
    public void ValidationKO() { model.ValidationKO(); }

    public void ReverificationOk() { model.ReverificationOk(); }
    public void ReverificationKO() { model.ReverificationKO(); }

    public void OuvertureSession() { model.OuvertureSession(); }
    public void FermetureSession() { model.FermetureSession(); }
    public void FermetureSessionKO() { model.FermetureSessionKO(); }

    public void CB() { model.CB(); }
    public void Espece() { model.Espece(); }
}
