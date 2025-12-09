/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CommentsRemoverTest {

  @Test
  void testStripMultiLineComments() {
    String description =
        """
				Vérifier que le ROR est capable d'interpréter et de répondre correctement à tous les types de requêtes pour la transaction de lecture, en appliquant la politique d'accès correspondante lorsqu'un profil utilisateur donnant accès aux données du Profil 1 est transmis.
				Pour atteindre cet objectif, nous avons 15 cas de test correspondant à chaque profil.
				Pour chaque suite de test, deux paramètres sont fixes : une date de référence est toujours renseignée et la RestrictionOI a la valeur 0.
				Des contrôles sont effectués pour vérifier la présence des attributs obligatoires du modèle d'exposition pour chaque requête.

				Contenu :
				Ce projet de test est composé d’une suite de test. Les tests réalisés correspondent à l’usage d’orientation en SSR et PA-PH ainsi que l’usage de régulation SAMU.

				Prérequis :
				Pour utiliser ce projet de test dans le ROR, veuillez suivre les instructions suivantes :
				• Si la valeur par défaut ne convient pas, renseignez l'Endpoint de votre application.
				• Créez ou modifiez une EJ de sorte qu'elle soit ouverte et contienne les éléments obligatoires suivants : idNat_Struct, la raison sociale, le statut juridique, le sous-ensemble agrégat statut juridique, ainsi que des métadonnées. Ajoutez également un numéro FINESS.
				• Dans cette EJ, créez une EG ouverte, comprenant les éléments obligatoires suivants : idNat_Struct, la dénomination de l'EG, une catégorie d'établissement, ainsi que des métadonnées. Ajoutez également un numéro FINESS.
				• Dans cette EG, créez 4 UE ouvertes, chacune décrite par un champ d'activité différent et comprenant les éléments obligatoires suivants : un identifiant, un nom, un type d'OI (unité élémentaire), un mode de prise en charge, une activité, un âge minimum et un âge maximum, ainsi que des métadonnées.
				• Renseignez l'IdNat_Struct demandé avec la valeur conforme pour l'EG créée ci-dessus.
				• Renseignez dateRef avec une valeur date-heure antérieure aux modifications demandées, au format aaaa-mm-jjThh:mm:ss.

				Paramètres à saisir par l’utilisateur :
				• Enpoint : par défaut, URL de l’application à tester
				• idNat_Struct : suivre les consignes décrites dans la description
				• dateRef : date-heure antérieure aux modifications demandées de façon à ce qu’elles apparaissent dans les résultats.
				Attention à bien respecter le format de date

				/*
				* — Fichiers Schematron à charger pour le test —
				* - validation_elements_nomenclatures.sch
				* - validation_metadonnees.sch
				* - validation_identifiants.sch
				* - validation_conformite_UE.sch
				* - filtre_profil1.sch
				*/
							""";
    assertEquals(2295, CommentsRemover.stripMultiLineComments(description).length());
  }

  @Test
  void testStripMultiLineCommentsStartingWithTab() {
    String description =
        """
Attention à bien respecter le format de date

	/*
	* — Fichiers Schematron à charger pour le test —
	* - validation_elements_nomenclatures.sch
	* - validation_metadonnees.sch
	* - validation_identifiants.sch
	* - validation_conformite_UE.sch
	* - filtre_profil1.sch
	*/
				""";
    assertEquals(44, CommentsRemover.stripMultiLineComments(description).length());
  }

  @Test
  void testStripMultiLineCommentsInTheMiddle() {
    String description =
        """
				Vérifier que le ROR est capable d'interpréter et de répondre correctement à tous les types de requêtes pour la transaction de lecture, en appliquant la politique d'accès correspondante lorsqu'un profil utilisateur donnant accès aux données du Profil 1 est transmis.
				Pour atteindre cet objectif, nous avons 15 cas de test correspondant à chaque profil.
				Pour chaque suite de test, deux paramètres sont fixes : une date de référence est toujours renseignée et la RestrictionOI a la valeur 0.
				Des contrôles sont effectués pour vérifier la présence des attributs obligatoires du modèle d'exposition pour chaque requête.

				/*
				* — Fichiers Schematron à charger pour le test —
				* - validation_elements_nomenclatures.sch
				* - validation_metadonnees.sch
				* - validation_identifiants.sch
				* - validation_conformite_UE.sch
				* - filtre_profil1.sch
				*/
				Contenu :
				Ce projet de test est composé d’une suite de test. Les tests réalisés correspondent à l’usage d’orientation en SSR et PA-PH ainsi que l’usage de régulation SAMU.

				Prérequis :
				Pour utiliser ce projet de test dans le ROR, veuillez suivre les instructions suivantes :
				• Si la valeur par défaut ne convient pas, renseignez l'Endpoint de votre application.
				• Créez ou modifiez une EJ de sorte qu'elle soit ouverte et contienne les éléments obligatoires suivants : idNat_Struct, la raison sociale, le statut juridique, le sous-ensemble agrégat statut juridique, ainsi que des métadonnées. Ajoutez également un numéro FINESS.
				• Dans cette EJ, créez une EG ouverte, comprenant les éléments obligatoires suivants : idNat_Struct, la dénomination de l'EG, une catégorie d'établissement, ainsi que des métadonnées. Ajoutez également un numéro FINESS.
				• Dans cette EG, créez 4 UE ouvertes, chacune décrite par un champ d'activité différent et comprenant les éléments obligatoires suivants : un identifiant, un nom, un type d'OI (unité élémentaire), un mode de prise en charge, une activité, un âge minimum et un âge maximum, ainsi que des métadonnées.
				• Renseignez l'IdNat_Struct demandé avec la valeur conforme pour l'EG créée ci-dessus.
				• Renseignez dateRef avec une valeur date-heure antérieure aux modifications demandées, au format aaaa-mm-jjThh:mm:ss.

				Paramètres à saisir par l’utilisateur :
				• Enpoint : par défaut, URL de l’application à tester
				• idNat_Struct : suivre les consignes décrites dans la description
				• dateRef : date-heure antérieure aux modifications demandées de façon à ce qu’elles apparaissent dans les résultats.
				Attention à bien respecter le format de date

							""";
    assertEquals(2295, CommentsRemover.stripMultiLineComments(description).length());
  }

  @Test
  void testStripMultiLineCommentswithoutStarsInside() {
    String description =
        """
Attention à bien respecter le format de date

	/*
	 — Fichiers Schematron à charger pour le test —
	 - validation_elements_nomenclatures.sch
	 - validation_metadonnees.sch
	 - validation_identifiants.sch
	 - validation_conformite_UE.sch
	 - filtre_profil1.sch
	*/
				""";
    assertEquals(44, CommentsRemover.stripMultiLineComments(description).length());
  }

  @Test
  void testStripMultiLineCommentswithoutEnd() {
    String description =
        """
Attention à bien respecter le format de date

	/*
	 — Fichiers Schematron à charger pour le test —
	 - validation_elements_nomenclatures.sch
	 - validation_metadonnees.sch
	 - validation_identifiants.sch
	 - validation_conformite_UE.sch
	 - filtre_profil1.sch
				""";
    assertEquals(44, CommentsRemover.stripMultiLineComments(description).length());
  }
}
