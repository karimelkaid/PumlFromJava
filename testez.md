# Génération de Diagramme UML

Ce guide vous montre comment générer un diagramme UML à partir de votre code en utilisant IntelliJ IDEA.

## Étapes à suivre

1. **Cloner le Projet** : Clonez ce projet dans votre espace de travail local.

2. **Installation et Configuration** :
    - Assurez-vous que vous avez IntelliJ IDEA installé.

3. **Ouverture de IntelliJ**:
   - Lancez IntelliJ IDEA dans le répertoire `p21_projet`.

4. **Création d'un Package**:
   - Dans le dossier `src/`, créez un nouveau package. Par exemple: `src/nomPackage`.

5. **Organisation des Classes**:
   - Placez vos classes et sous-packages dans le package créé (`src/nomPackage`).

6. **Configuration du Fichier `Java2Puml`**:
   - Ouvrez le fichier `src/pumlFromJava/Java2Puml`.
   - Dans le tableau `arguments`, modifiez la septième valeur (en commençant à 0) pour inclure le nom de votre package, `"nomPackage"`, contenant votre code source. Gardez les guillemets.

7. **Exécution du Programme**:
   - Exécutez le programme en cliquant sur la flèche verte située en haut à droite de l'interface IntelliJ.

8. **Génération du Fichier PlantUML**:
   - Un fichier PlantUML sera généré si nécessaire.
   - Ce fichier portera le même nom que le package créé à l'étape 2 et se trouvera dans le répertoire `uml/`.

## Conclusion

Suivez ces étapes pour générer un diagramme UML de votre projet, facilitant ainsi la visualisation et la compréhension de votre structure de code.
