package com.freedsuniverse.sidecraft.screen;

import javax.swing.JProgressBar;
import javax.swing.SpringLayout;
import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.SwingConstants;

import com.freedsuniverse.sidecraft.Main;

public class LoadingScreen extends Screen {
	private static final long serialVersionUID = 1L;
	private static final int HALF_HEIGHT = 20;
	
	private Runnable task;
	
	public LoadingScreen(Runnable r) {
		this(r, "Loading...");
	}
	
	/**
	 * @wbp.parser.constructor
	 */
	public LoadingScreen(Runnable r, String title) {
		this.task = r;
		int height = Main.getPaneHeight();
		
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		springLayout.putConstraint(SpringLayout.NORTH, progressBar, height / 2 - HALF_HEIGHT, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, progressBar, 93, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, progressBar, -height / 2 + HALF_HEIGHT, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, progressBar, -93, SpringLayout.EAST, this);
		add(progressBar);
		
		JLabel lblLoading = new JLabel(title);
		springLayout.putConstraint(SpringLayout.NORTH, lblLoading, 50, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, lblLoading, 116, SpringLayout.WEST, this);
		lblLoading.setHorizontalAlignment(SwingConstants.CENTER);
		springLayout.putConstraint(SpringLayout.EAST, lblLoading, -116, SpringLayout.EAST, this);
		lblLoading.setFont(new Font("Tahoma", Font.PLAIN, 26));
		add(lblLoading);
	}
	
	public void focus() {
		Main.getContentPane().add(this, LoadingScreen.class.getName());
		Main.setScreen(LoadingScreen.class.getName());
		new Thread(task).start();
	}
}