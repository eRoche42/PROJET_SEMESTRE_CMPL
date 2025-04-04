=============================
|     L3   Groupe   4.1     |
=============================
|  Elie ROCHE  |  Evan RIO  |
=============================
|      Compilateur  V2      |
=============================

Un des problèmes rencontrés concerne les BINCONDS, qui, une fois dans un contexte local (procédure), ne fonctionnent plus correctement.
Nous avons eu des difficultés avec la gestion du contenu et des affectations.

Plusieurs problèmes sont liés à l'utilisation de pileRep, notamment des dépilages et/ou empilages effectués au mauvais endroit.
Pour améliorer la compréhension et faciliter la gestion, nous avons créé des piles séparées.

Nous avons remarqué une erreur de branchement dans notre code lors de la compilation des pattern-matchings (cond).
Nous effectuions les branchements inconditionnels de sortie avant la fin du cond.
Nous donnions ipo - 2 en paramètre des bincond, alors qu'il fallait leur donner ipo + 1.
Nous avons corrigé les BINCONDS, qui doivent maintenant se mettre à jour normalement.

Un autre problème rencontré concerne l'utilisation des AFFCTERL et CONTENUL.
Malgré la mise à jour de bc et la bonne gestion des adresses liées aux variables ou paramètres, nous avons une mauvaise affectation.
Par exemple, dans polyP36-exo8, r est mis à jour, mais pas le booléen marq.
Nous avons effectué plusieurs tests pour comprendre le problème des affectations du booléen, mais nous n'avons pas encore trouvé de solution.

Le passage aux procédures nous a obligés à modifier le fonctionnement des conditionnelles et à revoir l'utilisation des variables dans PtGen,
ce qui nous permet de mieux comprendre le fonctionnement global.
Plusieurs erreurs, auparavant cachées par un fonctionnement basé sur un main unique sans appel, ont pu être corrigées.

Nous continuons à déboguer la partie affecter contenu, en utilisant MAPILE et en analysant les différents codes rendus.