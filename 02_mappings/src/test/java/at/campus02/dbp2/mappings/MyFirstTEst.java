package at.campus02.dbp2.mappings;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MyFirstTEst {

    @Test
    public void toUpperCaseConvertsAllLettersToUpperCase(){
        //given (voraussetzungen.. cosa devo genereare per testare)
        String teststring ="kleinGROSS";
        //when (se viene creato)
        String result=teststring.toUpperCase();
        //then   (se fa qualcosa)
        Assertions.assertEquals("KLEINGROSS",result,"qui posso scrivere un messagiio che compare quando test non funziiona");
    }
}
