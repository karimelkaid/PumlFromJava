package western;

//  @ Project : Western
//  @ File Name : Histoire2.java
//  @ Date : 02/02/2010
//  @ Author : P. Divoux
//  @ Date : 31/12/2022
//  @ Author : R. Schneider
//
//


public class Histoire2
{
    public static void main(String[] args)
    {
        Dame scarlett = new Dame("Scarlett");
        Cowboy luke = new Cowboy("Luke", new Boisson("bière", Genre.FEMININ));
        Brigand dalton = new Brigand("Dalton", new Boisson("whisky", Genre.MASCULIN));
        Narrateur narrateur = new Narrateur("Ed");

        /* Les présentations */
        narrateur.sePresenter();
        narrateur.dire("Il était une fois, à l'Ouest de Java Town, trois personnages singuliers :");
        scarlett.sePresenter();
        luke.sePresenter();
        dalton.sePresenter();

        /* Les libations */
        narrateur.dire("Après leur traversée du désert, nos trois personnages s'arrêtent dans un bar pour étancher leur soif.");
        luke.dire("Nous sommes arrivés. Trinquons, maintenant !");
        luke.boire();
        dalton.boire();
        dalton.dire("Waoh ! Il est fort celui-la !");
        scarlett.boire();
        scarlett.dire("C'est quand même plus rafraîchissant que l'alcool !");

        /* un peu d'action */
        narrateur.dire("Et maintenant, un peu d'action.");
        narrateur.dire(dalton.getNom() + " kidnappe " + scarlett.getNom() + ".");
        dalton.kidnapper(scarlett);
        narrateur.dire("Mais " + luke.getNom() + " intervient sans tarder et capture " + dalton.getNom() + ".");
        luke.tirerSur(dalton);
        luke.capturer(dalton);
    }
}

