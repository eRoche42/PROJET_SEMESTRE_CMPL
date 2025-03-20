programme test1: { if et expression simple}

const marqueur = -1;
var ent nblu, min, max;		{adresses variables dans la pile d'ex�cution de MAPILE: 0, 1, 2}
debut
	lire(min); max := min; lire(nblu);
		si nblu > max alors max := nblu
		sinon si nblu < min alors min := nblu fsi
		fsi;
		lire(nblu);
	
	ecrire(min, max);
fin
