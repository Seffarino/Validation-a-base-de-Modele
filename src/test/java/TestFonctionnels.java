import org.junit.Before;
import org.junit.Test;
import vbm.projet.ArticleDB;
import vbm.projet.Caisse;
import vbm.projet.FileFormatException;
import vbm.projet.Scanette;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests fonctionnels du système scanette + caisse.
 * Exemples de sessions d'usage.
 */
public class TestFonctionnels {

    // à modifier pour pointer sur le
    String FILE = "./target/classes/csv/produits.csv";

    // SUT : caisse + scanette
    Caisse caisse;
    Scanette scan;

    @Before
    public void setup() throws FileFormatException {
        ArticleDB database = new ArticleDB();
        database.init(new File(FILE));
        caisse = new Caisse(database);
        scan = new Scanette(database);
    }

    @Test
    public void test0() {
        // le client débloque sa scanette
        assertEquals(0, scan.debloquer());
        // il scanne un premier produit 
        assertEquals(0, scan.scanner(3520115810259l));
        // il tente de supprimer un produit qui n'est pas déjà scanné
        assertEquals(-2, scan.supprimer(5410188006711l));
        // il scanne un second produit (en double)
        assertEquals(0, scan.scanner(5410188006711l));
        assertEquals(0, scan.scanner(5410188006711l));
        // il supprime la 2e occurrence du produit
        assertEquals(0, scan.supprimer(5410188006711l));
        // transmission des achats à la caisse automatique : il ne doit pas y avoir d'erreur
        int r = scan.transmission(caisse);
        caisse.changerProbabiliteRelecture(1);   // force la relecture
        assertTrue(r == 0 || r == 1);
        if (r == 1) {
            // si une relecture est demandée, celle-ci doit s'effectuer sans erreur
            assertEquals(0, scan.scanner(3520115810259l));
            assertEquals(0, scan.scanner(5410188006711l));
            assertEquals(0, scan.transmission(caisse));
        }
        // le client paye 11 euros pour ses 2 produits (total 10.64)
        assertEquals(0.36, caisse.payer(11), 0.001);
    }

    @Test
    public void test1() {
        // le client débloque sa scanette
        assertEquals(0, scan.debloquer());
        // il scanne un premier produit
        assertEquals(0, scan.scanner(3520115810259l));
        // il se trompe et scanne un code barre erroné qui n'est pas connu
        assertEquals(-2, scan.scanner(1l));
        // pas de relecture au moment de la transmission
        caisse.changerProbabiliteRelecture(0);
        // au moment de la transmission, la caisse signale des articles inconnus.
        assertEquals(0, scan.transmission(caisse));
        // impossible de payer
        assertTrue(caisse.payer(13) < 0);
        // le caisser doit d'abord ouvrir une session pour éventuellement rescanner des articles
        assertEquals(0, caisse.ouvrirSession());
        assertEquals(0, caisse.scanner(5410188006711l));
        assertEquals(0, caisse.fermerSession());
        // une fois la session fermée, le client peut payer
        assertEquals(0.36, caisse.payer(11), 0.001);
    }

    @Test
    public void test2() {
        // le client débloque sa scanette
        assertEquals(0, scan.debloquer());
        // il va directement à la caisse
        assertEquals(0, scan.transmission(caisse));
        // échoue car la scanette est à nouveau bloquée après une transmission avec succès
        assertEquals(-1, scan.scanner(1l));
        // un autre client réutilise la même scanette
        scan.debloquer();
        scan.scanner(5410188006711l);
        // échec de la transmission car la caisse est en attente de la vérification du client précédent
        assertEquals(-1,scan.transmission(caisse));
        // le caisser doit d'abord ouvrir une session pour éventuellement rescanner des articles
        assertEquals(0, caisse.ouvrirSession());
        assertEquals(0, caisse.fermerSession());
        // la caisse ayant terminé la transaction (rien à payer), on peut traiter le second client
        caisse.changerProbabiliteRelecture(0);  // pas de relecture
        assertEquals(0, scan.transmission(caisse));
        // paiement de la somme exacte
        assertEquals(0, caisse.payer(2.15), 0.001);
    }

}