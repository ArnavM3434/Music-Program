import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.net.*;

import java.io.*;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SoundTemplate extends JFrame implements Runnable, ActionListener, AdjustmentListener
{
	JToggleButton button[][]=new JToggleButton[37][108]; //array of JToggleButtons

	JScrollBar tempoBar, volumeBar;


	JMenuBar menuBar = new JMenuBar();
	JMenu file, instrumentMenu, preBuilt, AdjustColumns;
	JMenuItem[] instrumentItems;
	String[] instrumentNames = {"Bell","Piano", "Oboe", "Glockenspiel"};


	JMenuItem save, load, song1, song2, song3, add20, add1, remove20, remove1;
	JButton stopPlay, clear, fillRandom, restart;
	JFileChooser fileChooser;
	JLabel[] labels = new JLabel[button.length];

	JScrollPane buttonPane; //allows for scrolling
	JPanel buttonPanel, labelPanel, tempoPanel, menuButtonPanel, volumePanel; //buttonPanel goes into scroll pane

	JLabel tempoLabel, volumeLabel;
	JPanel panel=new JPanel();
	JPanel bigOne = new JPanel();
	boolean notStopped=true;
	JFrame frame=new JFrame();
	String[] clipNames;
	Clip[] clip;
	int tempo = 1000;
	boolean playing= false;
	int row = 0, col = 0;
	Font font = new Font("Times New Roman", Font.PLAIN, 10);
	ImageIcon reset;

	float vol = 1;

	public SoundTemplate()
	{
		setSize(1000,800);
	  clipNames=new String[]{"C0","B1","ASharp1", "A1", "GSharp1", "G1", "FSharp1", "F1", "E1", "DSharp1", "D1", "CSharp1", "C1","B2","ASharp2", "A2", "GSharp2", "G2", "FSharp2", "F2", "E2", "DSharp2", "D2", "CSharp2", "C2", "B3","ASharp3", "A3", "GSharp3", "G3", "FSharp3", "F3", "E3", "DSharp3", "D3", "CSharp3", "C3"};


	  clip=new Clip[clipNames.length];
	  String initInstrument = "\\" + instrumentNames[0] + "\\" +instrumentNames[0];

      try {
         for(int x=0;x<clipNames.length;x++)
         {
         	URL url = this.getClass().getClassLoader().getResource(initInstrument+ " - " +clipNames[x] + ".wav");
         	AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
         	clip[x] = AudioSystem.getClip();
         	clip[x].open(audioIn);
		}

      } catch (UnsupportedAudioFileException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      } catch (LineUnavailableException e) {
         e.printStackTrace();

      }




		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(button.length, button[0].length, 2, 5)); //2 and 5 are amount of space between buttons

		for(int r = 0; r < button.length; r++)
		{
			String name = clipNames[r].replaceAll("Sharp", "#");
			for(int c = 0; c < button[0].length; c++)
			{
				button[r][c] = new JToggleButton();
				button[r][c].setFont(font);
				button[r][c].setText(name);
				button[r][c].setPreferredSize(new Dimension(30,30)); //each button is 30 x 30 square
				button[r][c].setMargin(new Insets(0,0,0,0)); //eliminates margins so that text isn't in center, it fits (shoved left)
				buttonPanel.add(button[r][c]);
			}
		}
		menuBar.setLayout(new GridLayout(1,3));
		file = new JMenu("File");
		save = new JMenuItem("Save");
		load = new JMenuItem("Load");
		save.addActionListener(this);
		load.addActionListener(this);
		file.add(save);
		file.add(load);
		menuBar.add(file);

		preBuilt = new JMenu("Pre-Made Songs");
		song1 = new JMenuItem("Pirates of the Caribbean");
		song2 = new JMenuItem("Happy Birthday!");
		song3 = new JMenuItem("Hot Cross Buns");
		song1.addActionListener(this);
		song2.addActionListener(this);
		song3.addActionListener(this);
		preBuilt.add(song1);
		preBuilt.add(song2);
		preBuilt.add(song3);
		menuBar.add(preBuilt);



		instrumentMenu = new JMenu("Instruments");
		instrumentItems = new JMenuItem[instrumentNames.length];
		for(int x = 0; x < instrumentNames.length; x++)
		{
			instrumentItems[x] = new JMenuItem(instrumentNames[x]);
			instrumentItems[x].addActionListener(this);
			instrumentMenu.add(instrumentItems[x]);
		}


		menuBar.add(instrumentMenu);



		AdjustColumns = new JMenu("Adjust Columns");
		add1 = new JMenuItem("Add 1");
		add1.addActionListener(this);
		add20 = new JMenuItem("Add 20");
		add20.addActionListener(this);
		remove1 = new JMenuItem("Remove 1");
		remove1.addActionListener(this);
		remove20 = new JMenuItem("Remove 20");
		remove20.addActionListener(this);
		AdjustColumns.add(add1);
		AdjustColumns.add(add20);
		AdjustColumns.add(remove1);
		AdjustColumns.add(remove20);
		menuBar.add(AdjustColumns);



		menuButtonPanel = new JPanel();
		menuButtonPanel.setLayout(new GridLayout(1,4));

		stopPlay = new JButton("Play");
		stopPlay.addActionListener(this);
		menuButtonPanel.add(stopPlay);

		clear = new JButton("Clear");
		clear.addActionListener(this);
		menuButtonPanel.add(clear);

		fillRandom = new JButton("Random");
		fillRandom.addActionListener(this);
		fillRandom.setFont(new Font("Dialog", Font.BOLD, 8));
		menuButtonPanel.add(fillRandom);

		restart = new JButton("Restart");
		restart.addActionListener(this);
		//restart.setFont(new Font("Times New Roman", Font.PLAIN, 10));
		reset = new ImageIcon("reset.png");
		reset = new ImageIcon(reset.getImage().getScaledInstance(30,30,Image.SCALE_SMOOTH));
		restart.setIcon(reset);
		menuButtonPanel.add(restart);




		menuBar.add(menuButtonPanel, BorderLayout.SOUTH);

		tempoBar = new JScrollBar(JScrollBar.HORIZONTAL, 200, 0, 100, 1000);
		tempoBar.addAdjustmentListener(this);
		tempo = tempoBar.getValue();
		tempoLabel = new JLabel(String.format("%s%2s", "Tempo: ", tempo));
		tempoPanel = new JPanel(new BorderLayout());
		tempoPanel.add(tempoLabel, BorderLayout.WEST);
		tempoPanel.add(tempoBar,BorderLayout.CENTER);

		volumeBar = new JScrollBar(JScrollBar.HORIZONTAL, 1000, 0, 0, 1000);
		volumeBar.addAdjustmentListener(this);
		vol = volumeBar.getValue()/1000f;
		volumeLabel = new JLabel(String.format("%s%2s", "Volume: ", vol));
		volumePanel = new JPanel(new BorderLayout());
		volumePanel.add(volumeLabel,  BorderLayout.WEST);
		volumePanel.add(volumeBar, BorderLayout.CENTER);

		bigOne.setLayout(new GridLayout(2,1));
		bigOne.add(tempoPanel);
		bigOne.add(volumePanel);





		buttonPane = new JScrollPane(buttonPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); //adds panel of buttons to the pane that can scroll
		this.add(buttonPane, BorderLayout.CENTER); //adds scroll pane to center of layout
		this.add(bigOne, BorderLayout.SOUTH);
		this.add(menuBar, BorderLayout.NORTH);


		String currDir = System.getProperty("user.dir");
		fileChooser = new JFileChooser(currDir);  //this is window that pops up and allows you to select different files


		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		Thread timing = new Thread(this);
		timing.start();

	}

	public void run()
	{
		do
		{
			try
			{
				if(!playing)
				{
					new Thread().sleep(0); //thread doesn't stop, there's just delay but clock is still running
				}
				else
				{
					for(int r = 0; r <button.length; r++)
					{
						if(button[r][col].isSelected())
						{

							FloatControl volume = (FloatControl)clip[r].getControl(FloatControl.Type.MASTER_GAIN);
							volume.setValue(5f * vol);
							clip[r].start();
							button[r][col].setForeground(Color.YELLOW);
						}
					}

					new Thread().sleep(tempo); //delay between each column
					for(int r=0;r<button.length;r++)
					{
						if(button[r][col].isSelected()) //whichever ones are selected have been played, and need to stop playing
						{
							clip[r].stop();
							clip[r].setFramePosition(0); //reset sound position
							button[r][col].setForeground(Color.BLACK);
						}
					}
					col++;
					if(col == button[0].length)
					col = 0; //start it back up again
				}

			}


			catch(InterruptedException e)
			{
			}


		}while(notStopped);

	}

	public void actionPerformed(ActionEvent e)
	{
		for(int y = 0; y < instrumentNames.length; y++)
		{
			if(e.getSource() == instrumentItems[y])
			{
				String selectedInstrument = "\\" + instrumentNames[y] + "\\" +instrumentNames[y];

				      try {
				         for(int x=0;x<clipNames.length;x++)
				         {
				         	URL url = this.getClass().getClassLoader().getResource(selectedInstrument+ " - " +clipNames[x] + ".wav");
				         	AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
				         	clip[x] = AudioSystem.getClip();
				         	clip[x].open(audioIn);
						 }

				      } catch (UnsupportedAudioFileException f) {
				         f.printStackTrace();
				      } catch (IOException f) {
				         f.printStackTrace();
				      } catch (LineUnavailableException f) {
				         f.printStackTrace();
     				  }

     				  col = 0;
     				  playing = false;
     				  stopPlay.setText("Play");
			  }
		  }

		  if(e.getSource() == clear)
		  {
			  for(int r = 0; r < button.length; r++)
			  {
				  for(int c = 0; c < button[0].length; c++)
				  {
					  button[r][c].setSelected(false);
				  }
			  }
			  col = 0;
			  playing = false;
			  stopPlay.setText("Play");

		  }

		  if(e.getSource() == stopPlay)
		  {
			  playing = !playing;
			  if(!playing)
				  stopPlay.setText("Play");
			  else stopPlay.setText("Stop");
		  }

		  if(e.getSource() == save)
		  {
			  saveSong();
		  }

		  if(e.getSource() == load)
		  {
			  //FileFilter filter = new FileNameExtensionFilter("*.txt", ".txt");
			  //fileChooser.setFileFilter(filter);
			  int returnVal = fileChooser.showOpenDialog(this);
			  if(returnVal == JFileChooser.APPROVE_OPTION) //if you select a file and then open it
			  {
				  try
				  {
					  File loadFile = fileChooser.getSelectedFile();
					  BufferedReader input = new BufferedReader(new FileReader(loadFile));
					  String temp;
					  temp = input.readLine(); //this is the first line(tempo and a bunch of spaces)
					  tempo = Integer.parseInt(temp.substring(0,3));
					  tempoBar.setValue(tempo);
					  char[][] song = new char[button.length][temp.length() - 3];

					  int r = 0;
					  while( (temp = input.readLine()) != null)
					  {
						  for(int c = 0; c < song[0].length; c++)
						  {
							  song[r][c] = temp.charAt(c);
						  }
						  r++;
					  }
					 	setNotes(song);
				  }catch(IOException ee)
				  {}
				  col = 0;
				  playing = false;
				  stopPlay.setText("Play");
			  }
		  }

		  if(e.getSource() == song1)
		  {

		  		  try
		  		  {
		  			File fileName = new File("PiratesOfCaribbean.txt");
		  			BufferedReader input = new BufferedReader(new FileReader(fileName));
		  			String temp;
		  			temp = input.readLine(); //this is the first line(tempo and a bunch of spaces)
		  			tempo = Integer.parseInt(temp.substring(0,3));
		  			tempoBar.setValue(tempo);
		  			char[][] song = new char[button.length][temp.length() - 3];

		  			int r = 0;
		  			while( (temp = input.readLine()) != null)
		  			{
		  			  for(int c = 0; c < song[0].length; c++)
		  			  {
		  				  song[r][c] = temp.charAt(c);
		  			  }
		  			  r++;
		  			}
		  				setNotes(song);
		  			}catch(IOException ee)
		  				  {}
		  				  col = 0;
		  				  playing = false;
		  				  stopPlay.setText("Play");
		  	}


		  if(e.getSource() == song2)
		  {

		  		  try
		  		  {
		  			File fileName = new File("HappyBirthday.txt");
		  			BufferedReader input = new BufferedReader(new FileReader(fileName));
		  			String temp;
		  			temp = input.readLine(); //this is the first line(tempo and a bunch of spaces)
		  			tempo = Integer.parseInt(temp.substring(0,3));
		  			tempoBar.setValue(tempo);
		  			char[][] song = new char[button.length][temp.length() - 3];

		  			int r = 0;
		  			while( (temp = input.readLine()) != null)
		  			{
		  			  for(int c = 0; c < song[0].length; c++)
		  			  {
		  				  song[r][c] = temp.charAt(c);
		  			  }
		  			  r++;
		  			}
		  				setNotes(song);
		  			}catch(IOException ee)
		  				  {}
		  				  col = 0;
		  				  playing = false;
		  				  stopPlay.setText("Play");
		  	}
		  if(e.getSource() == song3)
		  {

		  		  try
		  		  {
		  			File fileName = new File("HotCrossBuns.txt");
		  			BufferedReader input = new BufferedReader(new FileReader(fileName));
		  			String temp;
		  			temp = input.readLine(); //this is the first line(tempo and a bunch of spaces)
		  			tempo = Integer.parseInt(temp.substring(0,3));
		  			tempoBar.setValue(tempo);
		  			char[][] song = new char[button.length][temp.length() - 3];

		  			int r = 0;
		  			while( (temp = input.readLine()) != null)
		  			{
		  			  for(int c = 0; c < song[0].length; c++)
		  			  {
		  				  song[r][c] = temp.charAt(c);
		  			  }
		  			  r++;
		  			}
		  				setNotes(song);
		  			}catch(IOException ee)
		  				  {}
		  				  col = 0;
		  				  playing = false;
		  				  stopPlay.setText("Play");
		  	}

		  	if(e.getSource() == add1)
		  	{
				resizeButtons(1);
				playing = false;
				stopPlay.setText("Play");
			}

		  	if(e.getSource() == add20)
		  	{
				resizeButtons(20);
				playing = false;
				stopPlay.setText("Play");
			}

			if(e.getSource() == remove1)
			{
				if(button[0].length - 1 > 0)
				{
					resizeButtons(-1);
					playing = false;
					col = button[0].length - 1;
					stopPlay.setText("Play");
				}
			}
			if(e.getSource() == remove20)
			{
				if(button[0].length - 20 > 0)
				{
					resizeButtons(-20);
					playing = false;
					col = button[0].length - 20;
					stopPlay.setText("Play");
				}
			}

			if(e.getSource() == fillRandom)
			{
				playing = false;
				stopPlay.setText("Play");
				col = 0;
				fillRandom();
			}

			if(e.getSource() == restart)
			{
				playing = false;
				stopPlay.setText("Play");
				col = 0;
			}







	}



	public void fillRandom()
	{
		for(int r = 0; r < button.length; r++)
		{
			for(int c = 0; c < button[0].length; c++)
			{
				  button[r][c].setSelected(false);
			}
		}

		for(int r = 0; r < button.length; r++)
		{
			for(int c = 0; c < button[0].length; c++)
			{

				  int random = (int)(Math.random() * 10);
				  if(random <= 4)
				  button[r][c].setSelected(true);
			}
		}




	}



	public void resizeButtons(int change)
	{
		JToggleButton[][] temp = new JToggleButton[button.length][button[0].length + change];
		for(int r = 0; r < temp.length; r++)
		{
			for(int c = 0; c < temp[0].length; c++)
			{
				temp[r][c] = new JToggleButton();
				try
				{
					if(button[r][c].isSelected())
					temp[r][c].setSelected(true);
				}catch(ArrayIndexOutOfBoundsException e){}
			}
		}


				buttonPane.remove(buttonPanel);
				buttonPanel = new JPanel();
				button = new JToggleButton[temp.length][temp[0].length];
				buttonPanel.setLayout(new GridLayout(button.length, button[0].length));
				for(int r = 0; r < temp.length; r++)
				{
					String name = clipNames[r].replaceAll("Sharp", "#");
					for (int c = 0; c < button[0].length; c++)
					{
						button[r][c] = new JToggleButton();
						button[r][c].setFont(font);
						button[r][c].setText(name);
						button[r][c].setPreferredSize(new Dimension(30,30));
						button[r][c].setMargin(new Insets(0,0,0,0));
						buttonPanel.add(button[r][c]);
					}
				}
				this.remove(buttonPane);
				buttonPane = new JScrollPane(buttonPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
				this.add(buttonPane, BorderLayout.CENTER);


				for(int r = 0; r < temp.length; r++)
				{
					for(int c = 0; c < temp[0].length; c++)
					{
						try
						{
							if(temp[r][c].isSelected())
							button[r][c].setSelected(true);
						}
						catch (NullPointerException npe){}
						catch (ArrayIndexOutOfBoundsException ae){}
					}
				}
		this.revalidate();

	}


	public void setNotes(char[][] notes)
	{
		buttonPane.remove(buttonPanel);
		buttonPanel = new JPanel();
		button = new JToggleButton[37][notes[0].length];
		buttonPanel.setLayout(new GridLayout(button.length, button[0].length));
		for(int r = 0; r < button.length; r++)
		{
			String name = clipNames[r].replaceAll("Sharp", "#");
			for (int c = 0; c < button[0].length; c++)
			{
				button[r][c] = new JToggleButton();
				button[r][c].setFont(font);
				button[r][c].setText(name);
				button[r][c].setPreferredSize(new Dimension(30,30));
				button[r][c].setMargin(new Insets(0,0,0,0));
				buttonPanel.add(button[r][c]);
			}
		}
		this.remove(buttonPane);
		buttonPane = new JScrollPane(buttonPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.add(buttonPane, BorderLayout.CENTER);


		for(int r = 0; r < button.length; r++)
		{
			for(int c = 0; c < button[0].length; c++)
			{
				try
				{
					if(notes[r][c] == 'X')
					button[r][c].setSelected(true);
					else
					button[r][c].setSelected(false);
				}
				catch (NullPointerException npe){}
				catch (ArrayIndexOutOfBoundsException ae){}
			}
		}
		this.revalidate();







	}


	public void saveSong()
	{
		FileFilter filter = new FileNameExtensionFilter("*.txt", ".txt");
		fileChooser.setFileFilter(filter);
		if(fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
		{
			File file = fileChooser.getSelectedFile();
			try
			{
				String st = file.getAbsolutePath();
				if(st.indexOf(".txt")>= 0)
					st = st.substring(0, st.length() - 4);
				String output = "";

				for(int r = 0; r < button.length + 1; r++)
				{
					if(r == 0)
					{
						output += tempo;
						for(int x = 0; x < button[0].length;x++)
						output += " ";
					}
					else
					{
						for(int c = 0; c < button[0].length; c++)
						{
							if(button[r-1][c].isSelected())
								output += "X";
							else
								output += "-";
						}
					}
					output += "\n";
				}

				BufferedWriter outputStream = new BufferedWriter(new FileWriter(st + ".txt"));
				outputStream.write(output);
				outputStream.close();

			}catch(IOException exc)
			{

			}


		}


	}

	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		tempo = tempoBar.getValue();
		tempoLabel.setText(String.format("%s%2s", "Tempo: ", tempo));

		vol = volumeBar.getValue()/1000f;
		volumeLabel.setText(String.format("%s%2s", "Volume: ", vol));
	}


	public static void main(String args[])
	{
		SoundTemplate app=new SoundTemplate();

	}
}