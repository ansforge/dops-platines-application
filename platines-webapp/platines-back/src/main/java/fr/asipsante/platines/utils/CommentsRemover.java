/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class CommentsRemover {
  private static final String SPACE = " ";

  private static String TO_REMOVE = "To remove !!!";

  /** How does a multi line comment start? */
  private static final String COMMENT_ML_START = "/*";

  /** How does a multi line comment end? */
  private static final String COMMENT_ML_END = "*/";

  /**
   * Remove all the multi-line comments from a block of text
   *
   * @param text The text to remove multi-line comments from
   * @return The multi-line comment free text
   */
  public static String stripMultiLineComments(String text) {
    if (text == null) {
      return null;
    }

    try {
      StringBuffer output = new StringBuffer();

      // Comment rules:
      /*
       * / This is still a comment /* /*
       */
      // Comments do not nest
      // /* */ This is in a comment
      /*// */
      // The second // is needed to make this a comment.
      // First we strip multi line comments. I think this is important:
      boolean inMultiLine = false;
      BufferedReader in = new BufferedReader(new StringReader(text));
      while (true) {
        String line = in.readLine();
        String originalLine = line;
        if (line == null) {
          break;
        }

        if (!inMultiLine) {
          // We are not in a multi-line comment, check for a start
          int cstart = line.indexOf(COMMENT_ML_START);
          if (cstart >= 0) {
            // This could be a MLC on one line ...
            int cend = line.indexOf(COMMENT_ML_END, cstart + COMMENT_ML_START.length());
            if (cend >= 0) {
              // A comment that starts and ends on one line
              // BUG: you can have more than 1 multi-line comment
              // on a line
              line =
                  line.substring(0, cstart)
                      + SPACE
                      + line.substring(cend + COMMENT_ML_END.length());
            } else {
              // A real multi-line comment
              inMultiLine = true;
              line = line.substring(0, cstart) + SPACE;
            }
          } else {
            // We are not in a multi line comment and we havn't
            // started one so we are going to ignore closing
            // comments even if they exist.
          }
        } else {
          // We are in a multi-line comment, check for the end
          int cend = line.indexOf(COMMENT_ML_END);
          if (cend >= 0) {
            // End of comment
            line = line.substring(cend + COMMENT_ML_END.length());
            inMultiLine = false;
          } else {
            // The comment continues
            line = TO_REMOVE;
          }
        }
        String trimedLine = line.trim();

        if (line.equals(originalLine)) {
          output.append(trimedLine);
          output.append('\n');
        }
      }

      return output.toString().trim();
    } catch (IOException ex) {
      throw new IllegalArgumentException("IOException unexpected.");
    }
  }
}
