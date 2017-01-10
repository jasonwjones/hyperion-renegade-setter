package com.jasonwjones.hyperion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.essbase.api.base.EssException;
import com.essbase.api.base.IEssIterator;
import com.essbase.api.datasource.EssOutlineEditOption;
import com.essbase.api.datasource.IEssCube;
import com.essbase.api.metadata.IEssCubeOutline;
import com.essbase.api.metadata.IEssDimension;
import com.essbase.api.metadata.IEssMember;
import com.saxifrages.essbase.QuickCubeOp;
import com.saxifrages.essbase.QuickCubeOp.CubeOpDelegate;
import com.saxifrages.essbase.TargetUtils;
import com.saxifrages.essbase.primitives.CubeTarget;

/**
 * Main class for parsing arguments, then calling appropriate action.
 * 
 * @author jasonwjones
 *
 */
public class RenegadeUpdater {

	private static final Logger logger = LoggerFactory.getLogger(RenegadeUpdater.class);

	public static void main(String[] args) throws Exception {
		RenegadeCommand command = new RenegadeCommand();
		JCommander commander = new JCommander(command);
		try {
			commander.parse(args);
		} catch (ParameterException e) {
			commander.usage();
			System.exit(1);
		}

		if (!TargetUtils.isDefaultAvailable()) {
			logger.info("Provide a connection file in the current directory named {}", TargetUtils.DEFAULT);
			System.exit(1);
		}

		CubeTarget cubeTarget = TargetUtils.loadDefault();

		RenegadeUpdater updater = new RenegadeUpdater();
		updater.renegadeReport(cubeTarget);

		if (command.isUnset()) {
			logger.info("Attempting to remove renegade member");

			updater.removeRenegade(cubeTarget, command.getDimension());
		} else {
			if (null == command.getMember()) {
				logger.error("You must provide a member to set as renegade");
				System.exit(1);
			}
			logger.info("Attempting to update renegade member");
			updater.updateRenegade(cubeTarget, command.getDimension(), command.getMember());
		}

		updater.renegadeReport(cubeTarget);

	}

	public RenegadeUpdater() {
	}

	public void updateRenegade(CubeTarget cube, final String dimension, final String member) throws EssException {
		QuickCubeOp.execute(cube, new CubeOpDelegate<Boolean>() {

			public Boolean run(IEssCube cube) throws EssException {
				IEssCubeOutline outline = null;
				try {
					EssOutlineEditOption editOptions = new EssOutlineEditOption();
					outline = cube.openOutline(editOptions);

					IEssDimension products = outline.findDimension(dimension);
					IEssMember existingRenegade = products.getRenegadeMember();

					if (null != existingRenegade) {
						if (existingRenegade.getName().equals(member)) {
							logger.info("Renegade is already {}", member);
							return Boolean.FALSE;
						}
						logger.info("Removing existing renegade");
						products.setRenegadeMember(null);
					}
					logger.info("Validating given renegade: {}", member);
					IEssMember stuff = outline.findMember(member);
					if (stuff == null) {
						logger.error("{} does not seem to be a valid member name", member);
					} else {
						logger.info("Setting renegade and updating cube");
						products.setRenegadeMember(stuff);
						outline.save(IEssCube.EEssRestructureOption.KEEP_ALL_DATA);
					}
				} finally {
					if (outline != null) {
						outline.close();
					}
				}
				return Boolean.TRUE;
			}
		});
	}

	public void removeRenegade(CubeTarget cube, final String dimensionName) throws EssException {
		QuickCubeOp.execute(cube, new CubeOpDelegate<Boolean>() {
			public Boolean run(IEssCube cube) throws EssException {
				IEssCubeOutline outline = cube.openOutline(new EssOutlineEditOption());
				IEssDimension dimension = outline.findDimension(dimensionName);
				IEssMember existingRenegade = dimension.getRenegadeMember();

				if (null == existingRenegade) {
					logger.info("No existing renegade member on dimension {}", dimensionName);
					outline.close();
					return false;
				} else {
					logger.info("Removing renegade member (was {})", existingRenegade.getName());
					dimension.setRenegadeMember(null);
					outline.save(IEssCube.EEssRestructureOption.KEEP_ALL_DATA);
					return true;
				}
			}
		});
	}

	public void renegadeReport(CubeTarget cubeTarget) throws EssException {
		QuickCubeOp.execute(cubeTarget, new CubeOpDelegate<Boolean>() {
			public Boolean run(IEssCube cube) throws EssException {
				EssOutlineEditOption editOptions = new EssOutlineEditOption();
				IEssCubeOutline outline = cube.openOutline(editOptions);

				IEssIterator dimIter = outline.getDimensions();
				for (int dimIndex = 0; dimIndex < dimIter.getCount(); dimIndex++) {
					IEssDimension dim = (IEssDimension) dimIter.getAt(dimIndex);
					logger.info("{}", String.format("%-24s : %-24s", dim.getName(),
							dim.getRenegadeMember() != null ? dim.getRenegadeMember() : "<none>"));
				}
				outline.close();
				return true;
			}
		});
	}

}
