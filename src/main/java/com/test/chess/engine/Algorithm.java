package com.test.chess.engine;

import java.util.ArrayList;
import java.util.Comparator;

public class Algorithm {

    public ArrayList<Sequence> sequences = new ArrayList<>();

    private ArrayList<Move> movesGenerate = new ArrayList<>();
    private ArrayList<Move> movesDepth1 = new ArrayList<>();
    private ArrayList<Move> movesDepth2 = new ArrayList<>();
    private ArrayList<Move> movesDepth3 = new ArrayList<>();
    private ArrayList<Move> movesDepth4 = new ArrayList<>();

    private Board board = new Board();

    private int countMove = 0;

    public Algorithm() {
    }

    public Move makeMove(char color, char version) {

        GenerateSequences(color);

        CheckCheckmate(color);

        RemoveCheckmates();

        DepthBuild(4, color, version);

        Randomness(0.02, 0.002, 0.001);

        Sort(4);

        countMove += 1;

        return Decision();
    }

    //////MAIN METHODS/////

    public void GenerateSequences(char color) {

        sequences.clear();

        generateMoves(color, 1);

        for (int i = 0; i < movesDepth1.size(); i++) {

            sequences.add(new Sequence());
            sequences.get(i).set(0, movesDepth1.get(i));
        }

    }

    public void CheckCheckmate(char color) {

        double value1 = 0.0;
        double value2 = 0.0;

        char sideA = 'W';
        char sideB = 'B';

        if (color == 'B') {
            sideA = 'B';
            sideB = 'W';
        }

        Board save = new Board();
        save.copyBoard(this.board);

        for (int i = 0; i < sequences.size(); i++) {

            board.makeMove(sideA, sequences.get(i).move(0));

            value1 = board.value(sideA);

            if (value1 < -50) {

                sequences.get(i).checkmate = true;
            }

            this.generateMoves(sideB, 2);

            for (int j = 0; j < movesDepth2.size(); j++) {

                board.makeMove(sideB, movesDepth2.get(j));

                value2 = board.value(sideB);

                if (value2 > 50) {

                    sequences.get(i).check = true;
                }

                board.copyBoard(save);
                board.makeMove(sideA, sequences.get(i).move(0));
            }

            board.copyBoard(save);
        }
    }

    public void RemoveCheckmates() {

        //Removing sequences with Check or Checkmate

        for (int i = sequences.size() - 1; i >= 0; i--) {
            if (sequences.get(i).check) {
                sequences.remove(i);
            }
        }
    }

    public void Sort(int depth) {

        Comparator<Sequence> worthD4 = new Comparator<Sequence>() {
            @Override
            public int compare(Sequence o1, Sequence o2) {
                return (int) (o1.worthDepth4 * 10000 - o2.worthDepth4 * 10000);
            }
        };

        Comparator<Sequence> worthD8 = new Comparator<Sequence>() {
            @Override
            public int compare(Sequence o1, Sequence o2) {
                return (int) (o1.worthDepth8 * 10000 - o2.worthDepth8 * 10000);
            }
        };

        Comparator<Sequence> worthD12 = new Comparator<Sequence>() {
            @Override
            public int compare(Sequence o1, Sequence o2) {
                return (int) (o1.worthDepth12 * 10000 - o2.worthDepth12 * 10000);
            }
        };


        if (depth == 4) {

            sequences.sort(worthD4);

            //Comments:
            System.out.print("\nSORT\nmoves:" + sequences.size() + "\n");
            for (int i = 0; i < sequences.size(); i++) {
                sequences.get(i).move(0).introduce();
                System.out.print("\t" + sequences.get(i).worthDepth4 + "\t");
                System.out.print("\t" + sequences.get(i).check + " " + sequences.get(i).checkmate + "\t");
                sequences.get(i).showSequence();
                System.out.print("\n");
            }
        }

        if (depth == 8) {

            sequences.sort(worthD8);

            //Comments:
            System.out.print("\nSORT\nmoves:" + sequences.size() + "\n");
            for (int i = 0; i < sequences.size(); i++) {
                sequences.get(i).move(0).introduce();
                System.out.print("\t" + sequences.get(i).worthDepth8 + "\t");
                sequences.get(i).showSequence();
                System.out.print("\n");
            }
        }

        if (depth == 12) {

            sequences.sort(worthD12);

            //Comments:
            System.out.print("\nSORT\nmoves:" + sequences.size() + "\n");
            for (int i = 0; i < sequences.size(); i++) {
                sequences.get(i).move(0).introduce();
                System.out.print("\t" + sequences.get(i).worthDepth12 + "\t");
                sequences.get(i).showSequence();
                System.out.print("\n");
            }
        }
    }

    public void Randomness(double firstmove, double second, double game) {

        if (countMove == 0) {
            for (int i = 0; i < sequences.size(); i++) {
                sequences.get(i).worthDepth4 += (Math.random() - 0.5) * firstmove;
            }
        } else if (countMove == 1) {
            for (int i = 0; i < sequences.size(); i++) {
                sequences.get(i).worthDepth4 += (Math.random() - 0.5) * second;
            }
        } else {
            for (int i = 0; i < sequences.size(); i++) {
                sequences.get(i).worthDepth4 += (Math.random() - 0.5) * game;
            }
        }

    }

    public void DepthBuild(int depth, char side, char version) {

        double value1;
        double value2;
        double value3 = 0.0;
        double value4;
        double bestSideB = -200.0;
        double bestSideB_2 = -200.0;
        double bestWorth = 200.0;
        double worth = 200.0;
        double alpha = 200.0;
        double beta = 200.0;
        double bestBeta = -200.0;
        double worstBeta = 200.0;
        double avaregeBeta = 0.0;
        int counterBeta = 0;

        char sideA = 'W';
        char sideB = 'B';

        if (side == 'B') {
            sideA = 'B';
            sideB = 'W';
        }

        Board save = new Board();
        save.copyBoard(this.board);

        //ALGORITHM:

        for (int i = 0; i < sequences.size(); i++) {

            board.makeMove(sideA, sequences.get(i).move(depth - 4));

            Move tempSeqDepth3 = new Move();
            Move tempSeqDepth4 = new Move();
            Move tempSeqDepth4inside = new Move();

            value1 = board.value(sideA);

            this.generateMoves(sideB, 2);

            for (int j = 0; j < movesDepth2.size(); j++) {
                board.makeMove(sideB, movesDepth2.get(j));

                value2 = board.value(sideB);

                if (value2 > bestSideB) {

                    bestSideB = value2;
                }

                //beta:

                this.generateMoves(sideA, 3);

                for (int k = 0; k < movesDepth3.size(); k++) {

                    board.makeMove(sideA, movesDepth3.get(k));

                    value3 = board.value(sideA);

                    this.generateMoves(sideB, 4);

                    for (int l = 0; l < movesDepth4.size(); l++) {

                        board.makeMove(sideB, movesDepth4.get(l));

                        value4 = board.value(sideB);

                        if (value4 > bestSideB_2) {

                            bestSideB_2 = value4;

                            //sequence:
                            tempSeqDepth4inside = movesDepth4.get(l);

                        }

                        board.copyBoard(save);
                        board.makeMove(sideA, sequences.get(i).move(depth - 4));
                        if (depth == 6) {
                            board.makeMove(sideB, sequences.get(i).move(depth - 3));
                            board.makeMove(sideA, sequences.get(i).move(depth - 2));
                        }
                        board.makeMove(sideB, movesDepth2.get(j));
                        board.makeMove(sideA, movesDepth3.get(k));

                    }

                    if ((bestSideB_2 - value3 / 1000) < beta) {
                        beta = (bestSideB_2 - value3 / 1000);

                        //sequence
                        tempSeqDepth3 = movesDepth3.get(k);
                        tempSeqDepth4 = tempSeqDepth4inside;
                    }

                    //restart values
                    bestSideB_2 = -200.0;

                    board.copyBoard(save);
                    board.makeMove(sideA, sequences.get(i).move(depth - 4));
                    if (depth == 6) {
                        board.makeMove(sideB, sequences.get(i).move(depth - 3));
                        board.makeMove(sideA, sequences.get(i).move(depth - 2));
                    }
                    board.makeMove(sideB, movesDepth2.get(j));

                }

                if (beta > bestBeta) {

                    bestBeta = beta;
                    //setting sequence:
                    sequences.get(i).set(depth - 1, tempSeqDepth4);
                    sequences.get(i).set(depth - 2, tempSeqDepth3);
                    sequences.get(i).set(depth - 3, movesDepth2.get(j));
                }

                if (beta < worstBeta) {

                    worstBeta = beta;
                }

                avaregeBeta = (counterBeta * avaregeBeta + beta) / (1 + counterBeta);
                counterBeta++;

                //comments:
                //System.out.println((j+1)+"("+df.format(beta)+")");
                beta = 200.0;
                counterBeta = 0;

                //end of beta

                //restart to position before:
                board.copyBoard(save);
                board.makeMove(sideA, sequences.get(i).move(depth - 4));
                if (depth == 6) {
                    board.makeMove(sideB, sequences.get(i).move(depth - 3));
                    board.makeMove(sideA, sequences.get(i).move(depth - 2));
                }

            }


            alpha = (bestSideB - value1 / 100);

            if (version == 'a') {
                worth = alpha + bestBeta + avaregeBeta / 50 + worstBeta / 100;
            }
            if (version == 'b') {
                worth = alpha / 30 + bestBeta + avaregeBeta / 200;
                //NA RAZIE NAJLEPSZY! :worth = alpha/50 + bestBeta + avaregeBeta/50;
            }
            if (version == 'c') {
                worth = alpha / 3 + bestBeta;
                //worth = alpha/2 + bestBeta + avaregeBeta/10;
                //calkowicie bledne - popelnia dziwne ryzyka i ofiary (avarege beta!!)
            }
            if (version == 'd') {
                //TEST:
                worth = alpha + bestBeta + avaregeBeta / 250;
            }
            //1st ATTEMPT: worth = alpha + bestBeta + avaregeBeta + worstBeta/20;
            //good1: worth = alpha*5 + bestBeta + avaregeBeta/25 + worstBeta/50;
            //good2(bit defencive): worth = alpha + bestBeta + avaregeBeta/50 + worstBeta/100;


            sequences.get(i).worthDepth4 = worth;
            sequences.get(i).bestBeta4 = bestBeta;
            sequences.get(i).avrgBeta4 = avaregeBeta;


            if (worth < bestWorth) {
                bestWorth = worth;
                //selectedMove = movesDepth1.get(i);
            }

            //restart values
            bestSideB = -200.0;
            bestBeta = -200.0;
            worstBeta = 200.0;
            avaregeBeta = 0.0;

            board.copyBoard(save);
        }

        board.copyBoard(save);

    }

    public void DepthSecond(int search, int depth, char side, char version) {

        double value1;
        double value2;
        double value3 = 0.0;
        double value4;
        double bestSideB = -200.0;
        double bestSideB_2 = -200.0;
        double bestWorth = 200.0;
        double bestWorthSeq = 200.0;
        double worth = 200.0;
        double worthSeq = 200.0;
        double alpha = 200.0;
        double beta = 200.0;
        double bestBeta = -200.0;
        double worstBeta = 200.0;
        double avaregeBeta = 0.0;
        int counterBeta = 0;

        char sideA = 'W';
        char sideB = 'B';

        if (side == 'B') {
            sideA = 'B';
            sideB = 'W';
        }

        Board save = new Board();
        save.copyBoard(this.board);

        //ALGORITHM:

        for (int s = 0; s < search; s++) {

            //temporary moves for saving best sequences
            Move tempSeqDepth1 = new Move();
            Move tempSeqDepth2 = new Move();
            Move tempSeqDepth2inside = new Move();
            Move tempSeqDepth3last = new Move();
            Move tempSeqDepth3 = new Move();
            Move tempSeqDepth3inside = new Move();
            Move tempSeqDepth4last = new Move();
            Move tempSeqDepth4 = new Move();
            Move tempSeqDepth4inside = new Move();
            Move tempSeqDepth4inside2 = new Move();


            board.makeMove(sideA, sequences.get(s).move(depth - 8));
            board.makeMove(sideB, sequences.get(s).move(depth - 7));
            board.makeMove(sideA, sequences.get(s).move(depth - 6));
            board.makeMove(sideB, sequences.get(s).move(depth - 5));

            this.generateMoves(sideA, 1);

            for (int i = 0; i < movesDepth1.size(); i++) {

                board.makeMove(sideA, movesDepth1.get(i));


                value1 = board.value(sideA);

                this.generateMoves(sideB, 2);

                for (int j = 0; j < movesDepth2.size(); j++) {

                    board.makeMove(sideB, movesDepth2.get(j));

                    value2 = board.value(sideB);

                    if (value2 > bestSideB) {

                        bestSideB = value2;
                    }

                    //beta:

                    this.generateMoves(sideA, 3);

                    for (int k = 0; k < movesDepth3.size(); k++) {

                        board.makeMove(sideA, movesDepth3.get(k));

                        value3 = board.value(sideA);

                        this.generateMoves(sideB, 4);

                        for (int l = 0; l < movesDepth4.size(); l++) {

                            board.makeMove(sideB, movesDepth4.get(l));

                            value4 = board.value(sideB);

                            if (value4 > bestSideB_2) {

                                bestSideB_2 = value4;

                                //sequence:
                                tempSeqDepth4inside2 = movesDepth4.get(l);

                            }

                            board.copyBoard(save);
                            board.makeMove(sideA, movesDepth1.get(i));
                            board.makeMove(sideB, movesDepth2.get(j));
                            board.makeMove(sideA, movesDepth3.get(k));

                            //END OF movesDepth4
                        }

                        if ((bestSideB_2 - value3 / 1000) < beta) {
                            beta = (bestSideB_2 - value3 / 1000);

                            //sequence
                            tempSeqDepth3inside = movesDepth3.get(k);
                            tempSeqDepth4inside = tempSeqDepth4inside2;
                        }

                        //restart values
                        bestSideB_2 = -200.0;

                        board.copyBoard(save);
                        board.makeMove(sideA, movesDepth1.get(i));
                        board.makeMove(sideB, movesDepth2.get(j));

                        //END OF movesDepth3
                    }

                    if (beta > bestBeta) {

                        bestBeta = beta;
                        //setting sequence:

                        tempSeqDepth2inside = movesDepth2.get(j);
                        tempSeqDepth3 = tempSeqDepth3inside;
                        tempSeqDepth4 = tempSeqDepth4inside;
                    }

                    if (beta < worstBeta) {

                        worstBeta = beta;
                    }

                    avaregeBeta = (counterBeta * avaregeBeta + beta) / (1 + counterBeta);
                    counterBeta++;

                    //comments:
                    //System.out.println((j+1)+"("+df.format(beta)+")");
                    beta = 200.0;
                    counterBeta = 0;

                    //end of beta

                    //restart to position before:
                    board.copyBoard(save);
                    board.makeMove(sideA, movesDepth1.get(i));

                    //END OF movesDepth2
                }


                alpha = (bestSideB - value1 / 100);

                if (version == 'a') {
                    worth = alpha + bestBeta + avaregeBeta / 50 + worstBeta / 100;
                }
                if (version == 'b') {
                    worth = alpha / 30 + bestBeta + avaregeBeta / 200;
                    //The best for Now! :worth = alpha/50 + bestBeta + avaregeBeta/50;
                }
                if (version == 'c') {
                    worth = alpha / 3 + bestBeta;
                    //worth = alpha/2 + bestBeta + avaregeBeta/10;
                    //completly wrong - making strange risks (avarege beta!!)
                }
                if (version == 'd') {
                    //TEST:
                    worth = alpha + bestBeta + avaregeBeta / 250;
                }
                //1st ATTEMPT: worth = alpha + bestBeta + avaregeBeta + worstBeta/20;
                //good1: worth = alpha*5 + bestBeta + avaregeBeta/25 + worstBeta/50;
                //good2(bit defencive): worth = alpha + bestBeta + avaregeBeta/50 + worstBeta/100;


                if (worth < bestWorth) {
                    bestWorth = worth;

                    tempSeqDepth1 = movesDepth1.get(i);
                    tempSeqDepth2 = tempSeqDepth2inside;
                    tempSeqDepth3last = tempSeqDepth3;
                    tempSeqDepth4last = tempSeqDepth4;

                    worthSeq = bestWorth;

                }


                //restart values
                bestSideB = -200.0;
                bestBeta = -200.0;
                worstBeta = 200.0;
                avaregeBeta = 0.0;

                board.copyBoard(save);

                //END OF movesDepth1
            }


            if (worthSeq < bestWorthSeq) {
                bestWorthSeq = worthSeq;

                if (depth == 8) {
                    sequences.get(s).worthDepth8 = bestWorthSeq;
                }
                if (depth == 12) {
                    sequences.get(s).worthDepth12 = bestWorthSeq;
                }


                sequences.get(s).set(depth - 1, tempSeqDepth4last);
                sequences.get(s).set(depth - 2, tempSeqDepth3last);
                sequences.get(s).set(depth - 3, tempSeqDepth2);
                sequences.get(s).set(depth - 4, tempSeqDepth1);
            }


            // worth = 200;
            bestWorthSeq = 200;

            board.copyBoard(save);

            //END OF sequences
        }


        board.copyBoard(save);

    }


    public Move Decision() {

        //BlackIsChackmated
        Move BlackIsCheckmated = new Move(0, 0, 0, 0, 0, false, 100);

        Move Decision = BlackIsCheckmated;

        if (sequences.size() != 0) {
            Decision = sequences.get(0).move(0);
        }
        //WhiteisChackmated
        if (sequences.get(0).worthDepth4 < -50) {
            Decision.setCheckmate();
        }

        return Decision;
    }

    //////MAIN METHODS/////


    public void generateMoves(char color, int movesDepth) {
        board.createPositionsArray();

        movesGenerate.clear();

        if (color == 'W') {
            for (int i = 0; i < 19; i++) {
                movesGenerate.addAll(board.whitePieces[i].generateMoves(board.position, i, board.getLastMove()));
            }
        }

        if (color == 'B') {
            for (int i = 0; i < 19; i++) {
                movesGenerate.addAll(board.blackPieces[i].generateMoves(board.position, i, board.getLastMove()));
            }
        }

        switch (movesDepth) {
            case 1:
                movesDepth1.clear();
                movesDepth1.addAll(movesGenerate);
                break;
            case 2:
                movesDepth2.clear();
                movesDepth2.addAll(movesGenerate);
                break;
            case 3:
                movesDepth3.clear();
                movesDepth3.addAll(movesGenerate);
                break;
            case 4:
                movesDepth4.clear();
                movesDepth4.addAll(movesGenerate);
                break;
            default:
                break;
        }
    }

    public Board board() {

        return this.board;
    }

    public String listOfMoves(char color) {

        GenerateSequences(color);

        CheckCheckmate(color);

        RemoveCheckmates();

        String list = "";

        for (int i = 0; i < sequences.size(); i++) {

            list = list + (char) (sequences.get(i).move(0).getPos1_X() + 96) + sequences.get(i).move(0).getPos1_Y() + " - " +
                    (char) (sequences.get(i).move(0).getPos2_X() + 96) + sequences.get(i).move(0).getPos2_Y() + "     ";

            if ((i - 2) % 3 == 0) {
                list = list + "\n";
            }
        }
        return list;
    }

}
