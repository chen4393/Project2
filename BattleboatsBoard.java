/* 
I choose this code snippet because it is a simple but self-contained demo showing my interest in Java programming 
and this is a battle boats game which can be transplated into android, iOS and web platforms.
Implemented by Chaoran Chen	chen4393
*/

import java.util.Scanner;

public class BattleboatsBoard {
	int h;	// height
	int w;	// width
	int[][] board;	// the battle board
	// flags to determine whether each grid is attacked or not
	// true if visible by user
	boolean[][] isVisited;	
	int numBoats = 0;	// total number of boats
	int numSunk = 0;	// total number of sunk boat
	int boatIndex = 0;	// increment by one when new boat added, specifying each boat
	int numCannonShots = 0;	// total number of cannon shot

	boolean isDebug = false;	// debug mode flag
	
	// print the totally visible board before each turn
	// '0' stands for empty grid
	// positive numbers stand for the boat specified by its own boatIndex
	public void display() {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (isVisited[i][j])
					System.out.print(board[i][j] + "\t");
				else {
					if (isDebug)
						System.out.print(board[i][j] + "(I)\t");
					else
						System.out.print(board[i][j] + "\t");
				}
			}
			System.out.println();
		}
	}
	
	// print the partially visible board before each turn
	public void partialDisplay() {
		//System.out.println(prevResult);
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (isVisited[i][j]) {	// for visited cells
					if (board[i][j] < 0) {	// penalty, hit or sunk
						System.out.print(-1*board[i][j] + "\t");
					} else {	// miss
						System.out.print(board[i][j] + "\t");
					}
				} else {
					System.out.print("X" + "\t");	// for unvisited cells, invisible
				}
			}
			System.out.println();
		}
	}

	// construct the board and place boats
	public BattleboatsBoard(int row, int col) {
		h = row;
		w = col;
		board = new int[h][w];
		isVisited = new boolean[h][w];

		// determine the number of boats
		if (w == 3 || h == 3)
			numBoats = 1;
		else if ((w > 3 && w <= 5) || (h > 3 && h <= 5))
			numBoats = 2;
		else if ((w > 5 && w <= 7) || (h > 5 && h <= 7))
			numBoats = 3;
		else if ((w > 7 && w <= 9) || (h > 7 && h <= 9))
			numBoats = 4;
		else if ((w > 9 && w <= 12) || (h > 9 && h <= 12))
			numBoats = 6;

		for (int i = 0; i < numBoats; i++)
			placeBoat();	// set up the game by placing each boat
	}

	// place each boat
	public boolean placeBoat() {
		// temporary location array, 3 pairs of coordinates, row index first, column index second
		int[] tryBoard = new int[6];
		boatIndex++;	// increment by one when new boat added
		int attempts = 0;	// make sure not falling into infinite loop
		// flag of sucessful or not so that we can break the loop early and get next boat placement tryial
		boolean success = false;
		boolean isVertical = (Math.random() < 0.5);	// vertical or horizontal

		while (!success && attempts++ < 100) {
			int r = (int)Math.floor(Math.random() * h);	// random row index generated
			int c = (int)Math.floor(Math.random() * w);	// random column index generated
			int iTemp = 0;	// index of the temporary location array tryBoard
			success = true;
			// try to build a solution one step at a time
			// rebuild if fail
			while (success && iTemp < tryBoard.length) {
				// bound checking first!
				if (r >= h || c >= w) {	//bottom fail or right boundary fail
					success = false;
				} else if (board[r][c] == 0) {	//unused cell
					// bookkeep the temporary index
					tryBoard[iTemp] = r;
					tryBoard[iTemp+1] = c;
					if(isVertical)
						r++;
					else
						c++;
				} else {
					success = false;	// used cell location
				}
				iTemp += 2;	// go to next pair
			}
		}
		if (!success) {
			// 100 attempts failed, which should not happen
			System.out.println("Failed to placeBoat, attempts: " + attempts);
		}
		// copy the successful coordinates to the board and use boatIndex to mark that boat
		for (int i = 0; i < tryBoard.length; i += 2) {
			int row = tryBoard[i];
			int col = tryBoard[i+1];
			board[row][col] = boatIndex;	// use boatIndex to mark that boat
		}
		return success;
	}

	// get the user input, check it and print the board 
	public void play() {
		int turn = 0;
		String result = "";
		int r = 0, c = 0;
		
		boolean isRecon = false;	// recon flag
		Scanner sc = new Scanner(System.in);
		System.out.print("debug mode?(true/false):");	// type "true" if in debug mode
		if (sc.hasNext()) {
			isDebug = sc.nextBoolean();
		}
		while (numSunk != numBoats) {
			//System.out.println(numSunk + " : " + numBoats);
			turn++;
			System.out.println("Turn " + turn);
			if (isRecon) {
				System.out.println("RECON");
				drone(r, c);
				isRecon = false;
			}
			partialDisplay();
			/*	FOR DEBUG MODE */
			if (isDebug) {
				System.out.println();
				System.out.println("DEBUG");
				display();
			}
			sc = new Scanner(System.in);
			System.out.print("Please enter a row number: ");
			try {
				r = sc.nextInt();
			} catch (Exception e) {
				System.err.println("Invalid input, please enter an Integer");
				continue;
			}
			System.out.print("Please enter a column number: ");
			try {
				c = sc.nextInt();
			} catch (Exception e) {
				System.err.println("Invalid input, please enter an Integer");
				continue;
			}
			System.out.println(r + ", " + c);
			System.out.print("attack or recon?(attack/recon) ");
			String cmd = "";
			sc = new Scanner(System.in);
			if (sc.hasNext()) {
				cmd = sc.nextLine();	// parse the command "attack" or "recon"
			}
			// attack operation or recon operation
			if (cmd.equals("attack")) {
				if (r < 0 || r >= h || c < 0 || c >= w) {
					// shoot out of bounds, penalty
					turn++;
					result = "Penalty, the user’s next turn will be skipped.";
				} else {
					if (board[r][c] > 0) {
						if (checkSunk(board[r][c])) {
							result = "sunk";
							numSunk++;
						} else {
							result = "hit";
						}
						board[r][c] *= -1;	// mark negative number after hit
						numCannonShots++;
					} else if (isVisited[r][c]) {
						// If the user has already attacked that location
						// duplicated shot, penalty
						turn++;
						result = "Penalty, the user’s next turn will be skipped.";
					} else {
						result = "miss";
					}
					isVisited[r][c] = true;
				}
			} else if (cmd.equals("attack")){
				isRecon = true;
				turn += 4;
				result = "Penalty, the user’s next 4 turns will be skipped.";
			}
			System.out.println(result);
		}
		partialDisplay();
		if (isDebug) {
			System.out.println();
			System.out.println("DEBUG");
			display();
		}
		System.out.println("The total number of turns is " + turn);
		System.out.println("The total number of cannon shots is " + numCannonShots);
	}

	// check if the hit is the last hit
	public boolean checkSunk(int target) {
		int counter = 0;
		// scan through the whole table, find the frequency of the target appears
		for (int i = 0; i < board.length; i++)
			for (int j = 0; j < board[i].length; j++)
				if (board[i][j] == target)
					counter++;
		return counter == 1;	// check if the cell is the last part of the boat
	}
	// recon mode
	public void drone(int r, int c) {
		for (int i = r-1; i <= r+1; i++) {
			for (int j = c-1; j <= c+1; j++) {
				if (i >= 0 && j >= 0 && i < h && j < w) {
					System.out.println("(" + i + ", " + j + ") = " + board[i][j]);
				}
			}
		}
	}
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("usage: java BattleboatsBoard numOfRows numOfColumns");
			return;
		}
		int row = Integer.parseInt(args[0]);
		int col = Integer.parseInt(args[1]);
		if (row < 3 || col < 3 || row > 12 || col > 12) {
			System.out.println("Invalid input!\n" +
				"3 <= numOfRows <= 12, 3 <= numOfColumns <= 12");
			return;
		}
		//System.out.println(row + ", " + col);
		BattleboatsBoard bb = new BattleboatsBoard(row, col);
		bb.play();
	}
}
