package org.graalphp.language;

import org.eclipse.php.core.PHPVersion;
import org.eclipse.php.core.ast.error.BailoutErrorListener;
import org.eclipse.php.core.ast.error.ConsoleErrorListener;
import org.eclipse.php.core.ast.nodes.ASTParser;
import org.eclipse.php.core.ast.nodes.Program;
import org.graalphp.PhpLanguage;
import org.graalphp.parser.ParseScope;
import org.graalphp.parser.StmtVisitor;
import org.graalphp.parser.StmtVisitor.StmtVisitorContext;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.Assert;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * @author abertschi
 */
public class TestCommons {

    public static String php(String stmts) {
        return "<?php " + stmts + "?>";
    }

    private static final double DELTA = 2 >> 30;

    public static double evalInteger(long expected, String src) {
        Context ctx = Context.create("php");
        PhpLanguage.RETURN_LAST_EXPR = true;

        Value val = ctx.eval("php", php(src));
        System.out.println(val.toString());
        if (val.fitsInLong()) {
            Assert.assertEquals(expected, val.asLong());
        } else {
            Assert.assertEquals(expected, val.asDouble(), DELTA);
        }
        return val.asDouble();
    }

    public static boolean evalBoolean(boolean expected, String src) {
        Context ctx = Context.create("php");
        Value val = ctx.eval("php", php(src));
        System.out.println(val.toString());
        boolean res = val.asBoolean();
        Assert.assertEquals(expected, res);
        return res;
    }

    public static String compareStdout(String expected, String src) {
        return compareStdout(expected, src, true);
    }

    public static String compareStdout(String expected, String src, boolean addTags) {
        final ByteArrayOutputStream myOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(myOut));
        Context ctx = Context.create("php");
        ctx.eval("php", addTags ? php(src) : src);
        final String standardOutput = myOut.toString();
        Assert.assertEquals(expected, standardOutput);
        System.out.println(standardOutput);
        return standardOutput;
    }

    public static double evalDouble(double expected, String src) {
        return evalDouble(expected, src, DELTA);
    }

    public static double evalDouble(double expected, String src, double delta) {
        Context ctx = Context.create("php");
        Value val = ctx.eval("php", php(src));
        if (val.fitsInLong()) {
            Assert.assertEquals(expected, val.asLong(), delta);
            return val.asLong();
        } else {
            Assert.assertEquals(expected, val.asDouble(), delta);
            return val.asDouble();
        }
    }

    public static String inputStreamToString(InputStream in) {
        String text = null;
        try (Scanner scanner = new Scanner(in, StandardCharsets.UTF_8.name())) {
            text = scanner.useDelimiter("\\A").next();
        }
        return text;
    }

    public static Program parseProgram(String code, boolean addTags) throws Exception {
        if (addTags) {
            code = php(code);
        }
        ASTParser parser = ASTParser.newParser(PHPVersion.PHP7_4);
        parser.setSource(code.toCharArray());
        parser.addErrorListener(new ConsoleErrorListener());
        parser.addErrorListener(new BailoutErrorListener());
        Program pgm = parser.parsePhpProgram();
        return pgm;
    }

    public static StmtVisitorContext createTruffleAst(Program p) {
        StmtVisitor visitor = new StmtVisitor(null);
        StmtVisitor.StmtVisitorContext phpAst = visitor.createPhpAst(p,
                ParseScope.newGlobalScope());
        return phpAst;
    }
}
