/*
 * Copyright (c) 2009 - 2022, DHBW Mannheim - TIGERs Mannheim
 */
package edu.tigers.autoref.view.main;

import edu.tigers.autoreferee.engine.detector.EGameEventDetectorType;
import edu.tigers.sumatra.components.BetterScrollPane;
import edu.tigers.sumatra.components.EnumCheckBoxPanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Arrays;


public class AutoRefMainPanel extends JPanel
{
	private StartStopPanel startStopPanel = new StartStopPanel();
	private EnumCheckBoxPanel<EGameEventDetectorType> gameEventDetectorPanel;


	public AutoRefMainPanel()
	{
		gameEventDetectorPanel = new EnumCheckBoxPanel<>(EGameEventDetectorType.class, "游戏事件检测器",
				BoxLayout.PAGE_AXIS);
		gameEventDetectorPanel.addToggleAllButton();

		setupUI();
	}

	private void setupUI()
	{
		setLayout(new BorderLayout(10, 10));
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setBackground(new Color(248, 249, 250));

		// 创建带标题的模式控制面板
		JPanel modePanel = new JPanel(new BorderLayout());
		modePanel.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(),
			"运行模式",
			TitledBorder.LEFT,
			TitledBorder.TOP,
			new Font("微软雅黑", Font.BOLD, 12)
		));
		modePanel.setBackground(Color.WHITE);
		
		// 为StartStopPanel添加内边距
		JPanel startStopWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
		startStopWrapper.setBackground(Color.WHITE);
		startStopWrapper.add(startStopPanel);
		modePanel.add(startStopWrapper, BorderLayout.CENTER);
		
		add(modePanel, BorderLayout.NORTH);

		// 创建带标题的检测器面板
		JPanel detectorWrapperPanel = new JPanel(new BorderLayout());
		detectorWrapperPanel.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(),
			"事件检测器配置",
			TitledBorder.LEFT,
			TitledBorder.TOP,
			new Font("微软雅黑", Font.BOLD, 12)
		));
		detectorWrapperPanel.setBackground(Color.WHITE);

		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("insets 10", "[grow]", "[grow]"));
		panel.setBackground(Color.WHITE);
		panel.add(gameEventDetectorPanel, "grow x, top");
		
		final BetterScrollPane scrollPane = new BetterScrollPane(panel);
		scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
		scrollPane.getViewport().setBackground(Color.WHITE);
		
		detectorWrapperPanel.add(scrollPane, BorderLayout.CENTER);
		add(detectorWrapperPanel, BorderLayout.CENTER);
	}


	public StartStopPanel getStartStopPanel()
	{
		return startStopPanel;
	}


	public EnumCheckBoxPanel<EGameEventDetectorType> getGameEventDetectorPanel()
	{
		return gameEventDetectorPanel;
	}


	@Override
	public void setEnabled(final boolean enabled)
	{
		Arrays.asList(getComponents()).forEach(c -> c.setEnabled(enabled));
	}
}
