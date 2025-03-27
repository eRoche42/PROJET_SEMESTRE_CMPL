programme exo6:		{calcul de factorielle n}

var ent n, fn;

proc fact fixe (ent u) mod (ent fi)
	var ent fi1;
debut
	si u=1 alors sinon fi:=3 fsi;
fin;

debut
	fn := 8;
	lire(n);
	fact(n)(fn); 
	ecrire(fi);
fin
