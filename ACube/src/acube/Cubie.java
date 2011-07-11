package acube;

public final class Cubie {
  public static final int UF = 0;
  public static final int UR = 1;
  public static final int UB = 2;
  public static final int UL = 3;
  public static final int DF = 4;
  public static final int DR = 5;
  public static final int DB = 6;
  public static final int DL = 7;
  public static final int FR = 8;
  public static final int FL = 9;
  public static final int BR = 10;
  public static final int BL = 11;

  public static final int UFR = 0;
  public static final int URB = 1;
  public static final int UBL = 2;
  public static final int ULF = 3;
  public static final int DRF = 4;
  public static final int DFL = 5;
  public static final int DLB = 6;
  public static final int DBR = 7;

  public static final String[][] enames = {
    {"UF", "UR", "UB", "UL", "DF", "DR", "DB", "DL", "FR", "FL", "BR", "BL"},
    {"FU", "RU", "BU", "LU", "FD", "RD", "BD", "LD", "RF", "LF", "RB", "LB"},
  };

  public static final String[][] cnames = {
    {"UFR", "URB", "UBL", "ULF", "DRF", "DFL", "DLB", "DBR"},
    {"FRU", "RBU", "BLU", "LFU", "RFD", "FLD", "LBD", "BRD"},
    {"RUF", "BUR", "LUB", "FUL", "FDR", "LDF", "BDL", "RDB"},
    {"URF", "UBR", "ULB", "UFL", "DFR", "DLF", "DBL", "DRB"},
    {"FUR", "RUB", "BUL", "LUF", "RDF", "FDL", "LDB", "BDR"},
    {"RFU", "BRU", "LBU", "FLU", "FRD", "LFD", "BLD", "RBD"},
  };

  private Cubie() {}
}
