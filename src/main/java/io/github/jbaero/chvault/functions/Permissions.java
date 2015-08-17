package io.github.jbaero.chvault.functions;

import com.laytonsmith.abstraction.MCOfflinePlayer;
import com.laytonsmith.annotations.api;
import com.laytonsmith.core.Static;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.CBoolean;
import com.laytonsmith.core.constructs.CString;
import com.laytonsmith.core.constructs.Construct;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.environments.Environment;
import com.laytonsmith.core.exceptions.ConfigRuntimeException;
import com.laytonsmith.core.functions.Exceptions;
import com.laytonsmith.core.functions.Exceptions.ExceptionType;
import io.github.jbaero.chvault.CHVault.jFunction;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

/**
 * Permissions, 8/17/2015 1:32 AM
 *
 * @author jb_aero
 */
public class Permissions {

	public static String docs() {
		return "A set of functions for interacting with permissions plugins using Vault.";
	}

	public static Permission perms;

	public static Permission getPerms(Target t) throws ConfigRuntimeException {
		if (perms == null) {
			try {
				perms = Bukkit.getServicesManager().getRegistration(Permission.class).getProvider();
			} catch (NullPointerException npe) {
				throw new ConfigRuntimeException("Vault is not installed, Vault features cannot be used.",
						ExceptionType.InvalidPluginException, t);
			}
		}
		return perms;
	}

	public static OfflinePlayer op(MCOfflinePlayer src) {
		return (OfflinePlayer) src.getHandle();
	}

	@api
	public static class vault_has_permission extends jFunction {

		@Override
		public ExceptionType[] thrown() {
			return new ExceptionType[]{ExceptionType.FormatException, ExceptionType.InvalidPluginException};
		}

		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {
			MCOfflinePlayer user = Static.GetUser(args[0], t);
			String world = null;
			if (args.length == 3) {
				world = args[2].val();
			}
			return CBoolean.get(getPerms(t).playerHas(world, op(user), args[1].val()));
		}

		@Override
		public Integer[] numArgs() {
			return new Integer[]{2, 3};
		}

		@Override
		public String docs() {
			return "boolean {player, permission, [world]} Checks the permission value of a player, optionally in a"
					+ " specific world. When used on an offline player, the accuracy depends on the permission plugin.";
		}
	}

	@api
	public static class vault_pgroup extends jFunction {

		@Override
		public ExceptionType[] thrown() {
			return new ExceptionType[0];
		}

		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {
			CArray ret = new CArray(t);
			MCOfflinePlayer offlinePlayer = Static.GetUser(args[0], t);
			String world = null;
			if (args.length == 2) {
				world = args[1].val();
			}
			for (String group : getPerms(t).getPlayerGroups(world, op(offlinePlayer))) {
				ret.push(new CString(group, t));
			}
			return ret;
		}

		@Override
		public Integer[] numArgs() {
			return new Integer[]{1, 2};
		}

		@Override
		public String docs() {
			return "array {player, [world]} Returns an array of groups that the player is in. When used on offline"
					+ " players, the accuracy of this function is dependent on the permissions plugin.";
		}
	}
}
