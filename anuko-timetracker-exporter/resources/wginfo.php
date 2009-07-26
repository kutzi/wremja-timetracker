<?php
/** WR Time Tracker
*
* Copyright (c) 2004-2006 WR Consulting (http://wrconsulting.com)
*
* LIBERAL FREEWARE LICENSE: This source code document may be used
* by anyone for any purpose, and freely redistributed alone or in
* combination with other software, provided that the license is obeyed.
*
* There are only two ways to violate the license:
*
* 1. To redistribute this code in source form, with the copyright
*    notice or license removed or altered. (Distributing in compiled
*    forms without embedded copyright notices is permitted).
*
* 2. To redistribute modified versions of this code in *any* form
*    that bears insufficient indications that the modifications are
*    not the work of the original author(s).
*
* This license applies to this document only, not any other software
* that it may be combined with.
* 
* Contributors: Igor Melnik <igor.melnik at mail.ru>
* 
*/

// $Id: wginfo.php,v 1.0 2005/07/08 16:08:21 dries Exp $

/**
 * The script for interaction TimeTracker API and iGoogle gadget
 * @package TimeTracker
 */
	require_once('initialize.php');
	import('UserHelper');
	import('TimeHelper');
	import('DateAndTime');
	import('SysConfig');
	import('ProjectHelper');
	import('ActivityHelper');

	
	if ($GLOBALS["I18N"] && $_GET["lng"]) {
		$i18n = $GLOBALS["I18N"];
		$i18n->load($_GET["lng"]);
		$GLOBALS["I18N"] = &$i18n;
	}
		
        // inserts timesheet record without end time 
	function doStart($user, $cl_project, $cl_activity, $cl_date, $cl_start, $cl_notes) {
		$crdate = new DateAndTime('m/d/Y', $cl_date);
		TimeHelper::insert(array(
			'date' => $crdate->toString(DB_DATEFORMAT),
			'user_id' => $user->getActiveUser(),
			'project' => $cl_project,
			'activity' => $cl_activity,
			'start' => $cl_start,
			'finish' => '',
			'duration' => '',
			'note' => $cl_notes,
			'billable' => true));
	}

        // updates unfinished timesheet record
	function doFinish($user, $cl_project, $cl_activity, $cl_date, $cl_finish, $rec) {
		if (TimeHelper::toMinutes($cl_finish)==TimeHelper::toMinutes($rec["tfrom"])) {
			doDelete($user, $rec);
		} else {
			$crdate = new DateAndTime();
			$crdate->parseVal($rec["al_date"], DB_DATEFORMAT);
			
			$locktime = $user->getLocktime();
			$lockdate = $crdate->getClone();
			if ($locktime<0 || $locktime==null || $locktime=="") {
				$sc = new SysConfig(new User($user->getOwnerId(), false));
				$locktime = $sc->getValue(SYSC_LOCK_DAYS);	
			}
			if ($locktime>0) {
				$lockdate = new DateAndTime();
				$lockdate->decDay($locktime);
			}
			
			if ($crdate->before($lockdate)) {
				exit;
			}
			
			if (!TimeHelper::isValidTime($cl_finish)) {
	      		exit;
	    	}
	
			TimeHelper::update(array(
				'date' => $rec["al_date"],
				'ts' => $rec["al_timestamp"],
				'user_id' => $user->getActiveUser(),
				'project' => $rec["al_project_id"],
				'activity' => $rec["al_activity_id"],
				'start' => $rec["tfrom"],
				'finish' => $cl_finish,
				'duration' => '',
				'comment' => $rec["al_comment"],
				'billable' => true));
		}
	}

        // deletes timesheet record
	function doDelete($user, $rec) {
		TimeHelper::delete($user->getActiveUser(), $rec["al_timestamp"], $rec["al_date"]);
	}
	
	$cl_login		= $request->getParameter('login');
	$cl_password	= $request->getParameter('password');
	
	$auth->doLogin($cl_login, $cl_password);
	if ($auth->isAuthenticated()) {
		$user = new User($auth->getUserId());
		if ($user->isAdministrator()) {
			exit();
		}
	} else {
		exit();
	}
	
	$rec = TimeHelper::checkPresentTimeWithEmptyDuration($user->getUserId());
	
	$errors = array();
	$cl_project		= $request->getParameter('project');
	$cl_activity	= $request->getParameter('activity');
	$cl_action		= $request->getParameter('action');
	$cl_start		= $request->getParameter('start');
	$cl_finish		= $request->getParameter('finish');
	$cl_notes		= $request->getParameter('notes');
	$cl_date		= $request->getParameter('date');
	
	$crdate = new DateAndTime('%m/%d/%Y', $cl_date);
	if ($crdate->isError()) die("Date Error");
	
	if ($cl_action=="start") {
		if (empty($cl_project)) $errors[] = "Error: Select project";
		if (empty($cl_activity)) $errors[] = "Error: Select activity";
		if ($rec) {
			doFinish($user, $cl_project, $cl_activity, $cl_date, $cl_finish, $rec);
		}
		doStart($user, $cl_project, $cl_activity, $cl_date, $cl_start, $cl_notes);
		$rec = TimeHelper::checkPresentTimeWithEmptyDuration($user->getUserId());
	}
	
	if ($cl_action=="stop" && $rec) {
		doFinish($user, $cl_project, $cl_activity, $cl_date, $cl_finish, $rec);
		$rec = TimeHelper::checkPresentTimeWithEmptyDuration($user->getUserId());
	}

        // prepares data for response
	$project_list = ProjectHelper::findAllProjects($user, array('restrict' => true));
	$activity_list = ActivityHelper::findAllActivity($user);
	
	$week_time = TimeHelper::getTimePerWeek($user, $crdate);
	if (!$week_time) $week_time = "0:00";

	$daily_time = TimeHelper::getTotalTime($crdate->toString(DB_DATEFORMAT),$user->getActiveUser());
	if ($daily_time == false) $daily_time = "0:00";
	
	header('Content-Type: text/xml');

	/*$xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";*/
	$xml = '<info>';
	$xml .= '<projects>';
	foreach ($project_list as $project) {
		$xml .= '<project id="'.$project["p_id"].'"><![CDATA['.$project["p_name"].']]></project>';
	}
	$xml .= '</projects>';
	$xml .= '<activities>';
	foreach ($activity_list as $activity) {
		$res = array();
		foreach ($activity["aprojects"] as $project) $res[] = $project["p_id"];
		$xml .= '<activity id="'.$activity["a_id"].'" project="'.join(",",$res).'"><![CDATA['.$activity["a_name"].']]></activity>';
	}
	$xml .= '</activities>';
	$xml .= '<records>';
	if ($rec) {
		$xml .= '<incompleate project="'.$rec["al_project_id"].'" activity="'.$rec["al_activity_id"].'" start="'.$rec["tfrom"].'" date="'.$rec["al_date"].'">';
		$xml .= '<![CDATA['.$rec["al_comment"].']]>';
		$xml .= "</incompleate>\n";
	}
	$xml .= "<compleate daily_time=\"$daily_time\" week_time=\"$week_time\" />";
	$xml .= '</records>';
	$xml .= '<errors>';
	foreach ($errors as $error) {
		$xml .= '<error id="" message="'.$error.'"/>';
	}
	$xml .= '</errors>';
	$xml .= '</info>';
	
	print $xml;
?>