package uk.jamesleejones.tool;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

public class GenerateAST {
  public static void main(String[] args)
      throws FileNotFoundException, UnsupportedEncodingException {
    if (args.length != 1) {
      System.err.println("Usage: generate_ast <output directory>");
      System.exit(64);
    }
    String outputDir = args[0];

    defineAST(outputDir, "Expr", Arrays.asList(
        "Binary   : Expr left, Token operator, Expr right",
        "Grouping : Expr expression",
        "Literal  : Object value",
        "Unary    : Token operator, Expr right"
    ));
  }

  private static void defineAST(String outputDir, String baseName, List<String> types)
      throws FileNotFoundException, UnsupportedEncodingException {
    String path = outputDir + "/" + baseName + ".java";
    PrintWriter writer = new PrintWriter(path, "UTF-8");
    writer.println("package uk.jamesleejones.interp;");
    writer.println();
    writer.println("import java.util.List;");
    writer.println();
    writer.println("abstract class " + baseName + " {");

    defineVisitor(writer, baseName, types);

    // AST Classes
    for (String type : types) {
      String className = type.split(" ")[0].trim();
      String fields = type.split(":")[1].trim();
      defineType(writer, baseName, className, fields);
      writer.println();
    }

    // accept() method
    writer.println("  abstract <R> R accept(Visitor<R> visitor);");

    writer.println("}");
    writer.close();
  }

  private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
    writer.println("  interface Visitor<R> {");

    for (String type : types) {
      String typeName = type.split(" ")[0].trim();
      writer.println("    R visit" + typeName + baseName + "(" + typeName + " " + baseName.toLowerCase() + ");");
    }

    writer.println("    }");
  }

  private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
    writer.println("  static class " + className + " extends " + baseName + " {");

    String[] fields = fieldList.split(", ");

    // Fields
    for (String field : fields) {
      writer.println("    final " + field + ";");
    }

    writer.println();

    // Constructor
    writer.println("    " + className + "(" + fieldList + ") {");
    for (String field : fields) {
      String name = field.split(" ")[1];
      writer.println("      this." + name + " = " + name + ";");
    }
    writer.println("    }");

    // Visitor pattern
    writer.println();
    writer.println("    @Override");
    writer.println("    <R> R accept(Visitor<R> visitor) {");
    writer.println("      return visitor.visit" + className + baseName + "(this);");
    writer.println("    }");

    writer.println("  }");
  }
}
