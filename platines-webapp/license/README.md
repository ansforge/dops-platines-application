Ce module rend la ressource de licence disponible à travers le classpath 
indépendemment du chemin exact.
Ceci permet d'avoir un contrôle ou mise à jour des en-têtes de licence sur un module,
ce qui permet :

1 - d'aller plus vite quand on code exclusivement dans un module donné
2 - de favoriser un contrôle au file de l'eau plutôt qu'un échec de build sur la forge avec commit correctif.
