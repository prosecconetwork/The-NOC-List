# The-NOC-List

Created by Tony Veale for use by the CC community. No warranty is offered or implied. When publishing work based in whole or in part on these resources, please cite the following paper as a reference for the NOC approach and knowledge resource:

Veale, T. (2016). Round Up The Usual Suspects: Knowledge-Based Metaphor Generation. In Proceedings of the Meta4NLP Workshop on Metaphor at NAACL-2016, the annual meeting of the North American Association for Computational Linguistics. San Diego, California.

The NOC List

The Non-Official Characterization (NOC) List is a knowledge-base containing semantic triples about famous people, living and dead, fictional and real, for use in story generation, joke generation, metaphor generation and other computational creativity generation systems.

The NOC List is a large knowledge-base (approx. 30k triples) of pop-culture knowledge about famous individuals, whether living or dead and fictional or real. It was designed to act as a rich source of knowledge for automated metaphor generation and story-telling in Computational Creativity systems such as Twitterbots. This README describes the current state of the knowledge-base (which I call the NOC List, for Non-Official Characterizations List, in a nod to the McGuffin in the first Mission Impossible movie).

Also included are some easy-to-use Java classes for accessing and working with this knowledge in Java. With this data and these tools, even novice (Java) programmers will be able to do interesting idea generation in the pop-culture realm.

The CODE directory contains Java code for accessing the knowledge-base(s) and for using the knowledge-bases to generate tweets for @MetaphorMagnet. More of this below.

The DATA directory has two sub-directories that contain files which encode the knowledge in two-different formats. The TSV Lists directory stores each knowledge-base as a text file (.txt) that is tab delimited. The first line contains the field names for the file, also tab delimited. The directory called "Veale’s The NOC List" contains the corresponding data files in an easier-to-edit/browse spreadsheet (.xlsx) format. Notice that in lieu of a copyright message or author-attribution message each file contains the name of its creator and knowledge-engineer. Please keep this informal attribution in the file names if you share the data with others.

The slides in the README directory offer the best overall introduction to the knowledge-bases. A more detailed perspective is offered by the code itself. So let’s look more closely at that.

The class KnowledgeBaseModule.java is designed so that a single instance contains all the information in a single spreadsheet/TSV file. So a different instance is created for each of the data files in the knowledge-base. This is an extract from the class PersonOfInterest.java, which uses the data to generate interesting things:

NOC = new KnowledgeBaseModule(kDir + “Veale’s The NOC List.txt”, 0);

CATS = new KnowledgeBaseModule(kDir + “Veale’s Category Hierarchy.txt”, 0);

CLOTHES = new KnowledgeBaseModule(kDir + “Veale’s clothing line.txt”, 1);

CREATIONS = new KnowledgeBaseModule(kDir + “Veale’s creations.txt”, 0);

DOMAINS = new KnowledgeBaseModule(kDir + “Veale’s domains.txt”, 0);

WORLDS = new KnowledgeBaseModule(kDir + “Veale’s fictional worlds.txt”, 0);

VEHICLES = new KnowledgeBaseModule(kDir + “Veale’s vehicle fleet.txt”, 1);

WEAPONS = new KnowledgeBaseModule(kDir + “Veale’s weapon arsenal.txt”, 1);

PLACES = new KnowledgeBaseModule(kDir + “Veale’s place elements.txt”, 0);

SUPERLATIVES = new KnowledgeBaseModule(kDir + “superlatives.txt”, 0);

COMPARATIVES = new KnowledgeBaseModule(kDir + “comparatives.txt”, 0);

ANTONYMS = new KnowledgeBaseModule(kDir + “antonyms.txt”, 0);

PAST_PERFECTS = new KnowledgeBaseModule(kDir + “past perfects.txt”, 0);

Choose kDir to point to the TSV Lists subdirectory of the DATA directory and the above modules should load cleanly.

The last two data modules are a useful source of glue data for putting everything together in an NLP output.ANTONYMS maps properties (such as strong) onto their opposites (such as weak), while PAST_PERFECTS maps the present continuous form of action verbs (such as shooting) onto their past perfect forms (e,g. shot).

Have a look inside PersonOfInterest.java to see how these knowledge modules are used to generate a variety of different tweets, often anchored in an opposition of properties (provided by ANTONYMS).

For instance, the method generateDreamConflicts(…) generates tweets about dreams in which pop-culture figures appear. Here is an example of a two-parter:

Last night I dreamt I was cleaning floors with #GroundskeeperWillieMacDougal when we were run over with ruthless ambition by #HillaryClinton

I guess #HillaryClinton and #GroundskeeperWillieMacDougal represent warring parts of my personality: the capable vs. incompetent sides.


[The ANTONYMS module provides the crux of the dream here, the contrast between capable and incompetent]

The method generateNietzscheanTweets(…) generates what-if tweets that riff on Nietzsche’s famous line “What doesn’t kill you makes you stronger“, as in:


If what doesn’t kill you makes you stronger, shouldn’t being overwhelmed with ruthless ambition by #HillaryClinton make you more driven?

If what doesn’t kill you makes you stronger, shouldn’t being knocked out with an Oscar statuette by #DanielDayLewis make you more talented?


The method makeOthersLookGood(…) generates a relativistic comparison, where the speaker believes he/she resembles X but others say they resemble Y, where X and Y differ in some antonymous respect. Here’s an example:


I see myself as capable, but my boss says that I make even someone as incompetent as #EdWood look like #HillaryClinton.


The method generateShakespeareanTweets(…) generates what-if tweets that riff on the Shakespearean idea that “Clothes maketh the man”. Here is an example:


If clothes maketh the woman, would wearing #HillaryClinton’s pant suit make you more ambitious? Or more grasping?


The method walkMileInShoes(…) generates what-if tweets that riff on the old phrase “you shouldn’t judge someone until you’ve walked a mile in their shoes”. Here’s an example or three:


Nobody’s perfect! My grandma says to never judge a grasping first lady like #HillaryClinton until you have walked a mile in her pant suit.

Nobody’s perfect! My grandpa says to never judge a shallow diarist like #CarrieBradshaw until you have walked a mile in her Manolo Blahniks.

Nobody’s perfect! My mom says to never judge a neurotic actor like #JackNicholson until you have driven a mile in his Jokermobile.


The class PersonOfInterest also contains methods to generate different kinds of XYZ metaphor (i.e. of the form X is the Y of Z). Here are some examples:


What if #TheEmpireStrikesBack were real? #HillaryClinton could be its #PrincessLeiaOrgana: driven yet bossy, and controversial too

What if #TheNewTestament were about #AmericanPolitics? #MonicaLewinsky could be its #Lucifer: seductive yet power-hungry, and ruined too.

If #MonicaLewinsky is #Lucifer in a stained blue dress, who in #TheNewTestament is #HillaryClinton most like?

When it comes to #TheDemocrats, is #HillaryClinton just #UriahHeap in a pant suit? She is ambitious yet grasping, and controversial too


All of the knowledge in the NOC is stored as a collection of semantic triples. 
Now, one can use XML, RDF, RDFS or OWL to store a collection of semantic triples, but the core of the triple stays the same regardless of the formalism we use to encode it. So why bother with a complex syntactic sugar for the sake of formalistic appearances? When it comes to maintaining and editing and sharing a large body of semantic triples the most flexible format is a spreadsheet. 
As mundane as it may sound, a spreadsheet is perfect for this kind of representation work. 

Every cell, representing as it does a value-containing intersection of a named column and a named row, represents a single triple. We can share spreadsheets easily (and post them on the Web as Google docs for communal editing) and cut-and-paste relevant parts with abandon. 
So every piece of information in Scéalextricis to be found in a spreadsheet. Each sheet can be saved as a tab-separated-values text file for easy processing by Java and Python programs, and we encourage you to add new columns and rows to each one, and to create new spreadsheets of your own with complementary forms of knowledge.

Consider a semantic relationdhip P(X, Y) where P is a predicate that holds between X and Y.  We can represent a collection of triples of the form P(X, Y) in a spreadsheet with a column labelled P, a row whose first (and key) value is X, and a cell at the intersection of this row and the column labelled P that contains the value Y. This cell may contain multiple values Y1, Y2 ... Yn, each separated by commas, so this cell would represent a group of predications P(X, Y1), P(X, Y2) ... P(X, Yn)

The NOC distribution contains a variety of spreadsheets, each one a triple store that stores its semantic triples in this fashion. The following are the main files in the distribution:

Veale's The NOC List.xlsx

Veale's vehicle fleet.xlsx

Veale's weapon arsenal.xlsx

