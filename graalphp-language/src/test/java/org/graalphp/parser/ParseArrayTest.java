package org.graalphp.parser;

import org.eclipse.php.core.PHPVersion;
import org.eclipse.php.core.ast.error.BailoutErrorListener;
import org.eclipse.php.core.ast.error.ConsoleErrorListener;
import org.eclipse.php.core.ast.nodes.ASTParser;
import org.eclipse.php.core.ast.nodes.Program;
import org.graalphp.nodes.PhpStmtNode;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author abertschi
 */
public class ParseArrayTest {

    @Test
    public void parserTest() throws Exception {
        String src = TestCommons.inputStreamToString(getClass().getResourceAsStream("fannkuch.bak" +
                ".php"));
        System.out.println(src);
        ASTParser parser = ASTParser.newParser(PHPVersion.PHP7_4);
        parser.setSource(src.toCharArray());
        parser.addErrorListener(new ConsoleErrorListener());
        parser.addErrorListener(new BailoutErrorListener());
        Program pgm = parser.parsePhpProgram();
        System.out.println(pgm);
        StmtVisitor visitor = new StmtVisitor(null);
        StmtVisitor.StmtVisitorContext phpAst = visitor.createPhpAst(pgm,
                ParseScope.newGlobalScope());
        for (PhpStmtNode s : phpAst.getStmts()) {
            System.out.println(s.toString());
        }
        Assert.assertTrue(phpAst.getStmts().size() > 0);
    }

    //    @Test
    public void fannkuchExecTest() {
        String src = TestCommons.inputStreamToString(getClass().getResourceAsStream("fannkuch.bak" +
                ".php"));
        TestCommons.compareStdout("", src, false);
    }

    @Test(expected = Exception.class)
    public void arrayInvalidIndex() {
        // TODO in order to be php compliant, this test later on must work, array must be
        //  converted to map
        String code = TestCommons.php("$a = array(); $a[-1] = 1;");
        TestCommons.compareStdout("", code, false);

    }

    @Test()
    public void arrayAccessSimple() {
        String code = TestCommons.php("$a = array(); $a[0] = 1337; print($a[0]);");
        TestCommons.compareStdout("1337", code, false);

    }

    @Test()
    public void arrayGeneralizeTest() {
        String code = TestCommons.php("$a = array(); $a[0] = 1337; $a[1] = 1.1; print($a[0]); " +
                "print($a[1])");
        TestCommons.compareStdout("13371.1", code, false);
    }

    @Test()
    public void arrayGeneralizeAndGrow() {
        String code = TestCommons.php("$a = array(); " +
                "for($i = 0; $i < 10000; $i ++){$a[$i] = 1337;}; $a[$i]  = 1.1; print($a[$i]);" +
                "for($i; $i < 10000; $i ++){$a[$i] = 1337;}; print($a[$i - 1]);");
        TestCommons.compareStdout("1.11337", code, false);
    }

    @Test
    public void arrayTestSpecialization() {
        String code = TestCommons.php("$n = 30; $A = array();" +
                "for($i = 0; $i < $n; $i ++) {" +
                " $A[$i] = $i;" +
                "}" +
                "for($i; $i <  (2 * $n); $i ++) {" +
                " $A[$i] = $i + 0.1;" +
                "}");
        TestCommons.compareStdout("", code, false);
    }

    // Arrays are by default copied. We currently do not copy them
    @Test
    public void array2DTestSimple() {
        TestCommons.compareStdout("1337",
                "$A = array(); $B = array(); $B[0] = 1337; $A[0] = $B; print($A[0][0]);");
    }

    // TODO: this is supposed to fail if php compliant
    //    @Test(expected = Exception.class)
    public void arrayReadEmpty() {
        String code = TestCommons.php("$a = array();  print($a[0]);");
        TestCommons.compareStdout("", code, false);

    }
}
