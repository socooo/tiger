# tiger
A Mini Java compiler
The grammar of the Mini Java:

  Program → MainClass ClassDecl*
 MainClass → class id { public static void main ( String [] id )
               { Statement }}
 ClassDecl → class id { VarDecl* MethodDecl* }
           → class id extends id { VarDecl* MethodDecl* }
   VarDecl → Type id ;
MethodDecl → public Type id ( FormalList )
               { VarDecl* Statement* return Exp ;}
FormalList → Type id FormalRest*
           →
FormalRest →, Type id
      Type → int []
           → boolean
           → int
           → id
 Statement → { Statement* }
           → if ( Exp ) Statement else Statement
           → while ( Exp ) Statement
           → System.out.println ( Exp ) ;
           → id = Exp ;
           → id [ Exp ]= Exp ;
       Exp → Exp op Exp
           → Exp [ Exp ]
           → Exp . length
           → Exp . id ( ExpList )
           → INTEGER LITERAL
           → true
           → false
           → id
           → this
           → new int [ Exp ]
           → new id ()
           → ! Exp
           → ( Exp )
   ExpList → Exp ExpRest*
           →
  ExpRest  →  ,Exp
