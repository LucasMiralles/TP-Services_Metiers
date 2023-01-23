package comptoirs.service;

import comptoirs.dao.CommandeRepository;
import comptoirs.dao.ProduitRepository;
import comptoirs.entity.Commande;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
 // Ce test est basé sur le jeu de données dans "test_data.sql"
class CommandeServiceTest {
    private static final String ID_PETIT_CLIENT = "0COM";
    private static final String ID_GROS_CLIENT = "2COM";
    private static final String VILLE_PETIT_CLIENT = "Berlin";
    private static final BigDecimal REMISE_POUR_GROS_CLIENT = new BigDecimal("0.15");
    static final int NUMERO_COMMANDE_PAS_LIVREE = 99998;

    static final int NUMERO_COMMANDE_PAS_LIVREE2 = 1000;

    @Autowired
    private CommandeService service;

    @Autowired
    private ProduitRepository produit;

    @Autowired
    private CommandeRepository commandeDao;

    @Test
    void testCreerCommandePourGrosClient() {
        var commande = service.creerCommande(ID_GROS_CLIENT);
        assertNotNull(commande.getNumero(), "On doit avoir la clé de la commande");
        assertEquals(REMISE_POUR_GROS_CLIENT, commande.getRemise(),
            "Une remise de 15% doit être appliquée pour les gros clients");
    }

    @Test
    void testCreerCommandePourPetitClient() {
        var commande = service.creerCommande(ID_PETIT_CLIENT);
        assertNotNull(commande.getNumero());
        assertEquals(BigDecimal.ZERO, commande.getRemise(),
            "Aucune remise ne doit être appliquée pour les petits clients");
    }

    @Test
    void testCreerCommandeInitialiseAdresseLivraison() {
        var commande = service.creerCommande(ID_PETIT_CLIENT);
        assertEquals(VILLE_PETIT_CLIENT, commande.getAdresseLivraison().getVille(),
            "On doit recopier l'adresse du client dans l'adresse de livraison");
    }
    @Test
    void testDecrementer() {
        var prod = produit.findById(98).orElseThrow();
        int stockAvant = prod.getUnitesEnStock();
        service.enregistreExpédition(NUMERO_COMMANDE_PAS_LIVREE);
        prod = produit.findById(98).orElseThrow();
        assertEquals(stockAvant-20, prod.getUnitesEnStock(), "Le stock a été décrémenté de 20");
    }
    @Test
    void testEnregistrerDateLivraison() {
        Commande commande = commandeDao.findById(NUMERO_COMMANDE_PAS_LIVREE2).orElseThrow();
        assertNull(commande.getEnvoyeele(), "La commande ne doit pas être déjà livrée");
        commande = service.enregistreExpédition(NUMERO_COMMANDE_PAS_LIVREE2);
        assertNotNull(commande.getEnvoyeele(), "La commande doit être livrée");
        }
    }

