package comptoirs.service;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import comptoirs.dao.ProduitRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.validation.ConstraintViolationException;

@SpringBootTest
 // Ce test est basé sur le jeu de données dans "test_data.sql"
class LigneServiceTest {
    static final int NUMERO_COMMANDE_DEJA_LIVREE = 99999;
    static final int NUMERO_COMMANDE_PAS_LIVREE  = 99998;
    static final int REFERENCE_PRODUIT_DISPONIBLE_1 = 93;
    static final int REFERENCE_PRODUIT_DISPONIBLE_2 = 94;
    static final int REFERENCE_PRODUIT_DISPONIBLE_3 = 95;
    static final int REFERENCE_PRODUIT_DISPONIBLE_4 = 96;
    static final int REFERENCE_PRODUIT_DISPONIBLE_5 = 99;
    static final int REFERENCE_PRODUIT_INDISPONIBLE = 97;
    static final int UNITES_COMMANDEES_AVANT = 0;

    @Autowired
    LigneService service;

    @Autowired
    ProduitRepository produitDao;

    @Autowired
    CommandeService com;

    @Test
    void onPeutAjouterDesLignesSiPasLivre() {
        var ligne = service.ajouterLigne(NUMERO_COMMANDE_PAS_LIVREE, REFERENCE_PRODUIT_DISPONIBLE_1, 1);
        assertNotNull(ligne.getId(),
        "La ligne doit être enregistrée, sa clé générée"); 
    }

    @Test
    void laQuantiteEstPositive() {
        assertThrows(ConstraintViolationException.class, 
            () -> service.ajouterLigne(NUMERO_COMMANDE_PAS_LIVREE, REFERENCE_PRODUIT_DISPONIBLE_1, 0),
            "La quantite d'une ligne doit être positive");
    }
    @Test
    void testIncrementerQuantiteTotale(){
        var prod = produitDao.findById(REFERENCE_PRODUIT_DISPONIBLE_5).orElseThrow();
        int commandeAvant = prod.getUnitesCommandees();
        service.ajouterLigne(NUMERO_COMMANDE_PAS_LIVREE, REFERENCE_PRODUIT_DISPONIBLE_5, 10);
        prod = produitDao.findById(REFERENCE_PRODUIT_DISPONIBLE_5).orElseThrow();
        assertEquals(commandeAvant+10, prod.getUnitesCommandees(), "10 produits d'id 98 ont été commandé en plus");
    }
    @Test
    void testAjouterLigneSiProduitPasEnStock() throws Exception{
        assertThrows(Exception.class,
                () -> service.ajouterLigne(NUMERO_COMMANDE_PAS_LIVREE, REFERENCE_PRODUIT_INDISPONIBLE, 3),
                "Quantité demandée indisponible");
    }
}
