package com.test.chess.engine;

import java.util.ArrayList;
import java.util.Comparator;

public class Algorithm {

    private ArrayList<Sequence> sequences = new ArrayList<>();

    private ArrayList<Move> movesGenerate = new ArrayList<>();
    private ArrayList<Move> movesDepth1 = new ArrayList<>();
    private ArrayList<Move> movesDepth2 = new ArrayList<>();
    private ArrayList<Move> movesDepth3 = new ArrayList<>();
    private ArrayList<Move> movesDepth4 = new ArrayList<>();

    private Board board = new Board();

    public Algorithm() {
    }

    public Move makeMove(char color, char version) {

        GenerateSequences(color);

        DepthBuild(4, color, version);
        Sort(4);

        return Decision();
    }

    //////MAIN METHODS/////

    public void GenerateSequences(char color) {

        sequences.clear();

        generateMoves(color, 1);

        for (int i=0; i<movesDepth1.size(); i++) {

            sequences.add(new Sequence());
            sequences.get(i).set(0, movesDepth1.get(i));
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

        if(depth == 4) {

            sequences.sort(worthD4);

            //Comments:
            System.out.print("\nSORT\nmoves:"+sequences.size()+"\n");
            for(int i=0; i<sequences.size(); i++){
                sequences.get(i).move(0).introduce();
                System.out.print("\t"+sequences.get(i).worthDepth4+"\t");
                sequences.get(i).showSequence();
                System.out.print("\n");
            }
        }

        if(depth == 8) {

            sequences.sort(worthD8);

            //Comments:
            System.out.print("\nSORT\nmoves:"+sequences.size()+"\n");
            for(int i=0; i<sequences.size(); i++){
                sequences.get(i).move(0).introduce();
                System.out.print("\t"+sequences.get(i).worthDepth8+"\t");
                sequences.get(i).showSequence();
                System.out.print("\n");
            }
        }

        if(depth == 12) {

            sequences.sort(worthD12);

            //Comments:
            System.out.print("\nSORT\nmoves:"+sequences.size()+"\n");
            for(int i=0; i<sequences.size(); i++){
                sequences.get(i).move(0).introduce();
                System.out.print("\t"+sequences.get(i).worthDepth12+"\t");
                sequences.get(i).showSequence();
                System.out.print("\n");
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

            board.makeMove(sideA, sequences.get(i).move(depth-4));

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
                        board.makeMove(sideA, sequences.get(i).move(depth-4));
                        if(depth==6){
                            board.makeMove(sideB, sequences.get(i).move(depth-3));
                            board.makeMove(sideA, sequences.get(i).move(depth-2));
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
                    board.makeMove(sideA, sequences.get(i).move(depth-4));
                    if(depth==6){
                        board.makeMove(sideB, sequences.get(i).move(depth-3));
                        board.makeMove(sideA, sequences.get(i).move(depth-2));
                    }
                    board.makeMove(sideB, movesDepth2.get(j));

                }

                if (beta > bestBeta) {

                    bestBeta = beta;
                    //setting sequence:
                    sequences.get(i).set(depth-1, tempSeqDepth4);
                    sequences.get(i).set(depth-2, tempSeqDepth3);
                    sequences.get(i).set(depth-3, movesDepth2.get(j));
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
                board.makeMove(sideA, sequences.get(i).move(depth-4));
                if(depth==6){
                    board.makeMove(sideB, sequences.get(i).move(depth-3));
                    board.makeMove(sideA, sequences.get(i).move(depth-2));
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
                worth = alpha + bestBeta + avaregeBeta/250;
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


    public Move Decision() {

        return sequences.get(0).move(0);
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

    public Board board(){

        return this.board;
    }

}