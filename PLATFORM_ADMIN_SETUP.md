# Platform Super Admin dashboard setup

This project includes the first Platform Super Admin dashboard integration.

## Requirements in Supabase

The authenticated Supabase project must already have:

- `public.platform_admins` with the current platform admin's `user_id` row.
- `public.get_platform_colleges()` returning `college_id`, `college_name`, `short_name`, `city`, `state`, `is_active`, and `admin_count`.
- `public.assign_initial_college_admin(p_college_id uuid, p_user_id uuid)`.

These functions must remain protected by their server-side `auth.uid()` / `platform_admins` checks. Do not remove those checks to make SQL Editor tests pass; SQL Editor does not represent the signed-in Android user's JWT.

## Open and run

1. Extract the ZIP to a new folder.
2. Open the extracted `SportOrganizer` folder in Android Studio.
3. Restore your private `local.properties` from your own backup. It should define `SUPABASE_URL` and `SUPABASE_PUBLISHABLE_KEY`. Do not commit or share it.
4. Allow Gradle sync and run the app.
5. Sign in with the account whose auth user ID is present in `public.platform_admins`.
6. Open **Profile** and choose **Platform Super Admin Dashboard**.

## Assigning the first college admin

For an active college with zero admins, choose **Assign first admin** and enter the registered target user's Supabase Auth user UUID. The user UUID can be found in Supabase Authentication > Users. Do not enter the email address in this field. Supabase validates the role and college conditions in the RPC.

## Current scope

- Detects the current user's platform-admin row.
- Shows a dashboard entry only when that check succeeds.
- Loads the platform college list via the protected `get_platform_colleges()` RPC.
- Shows college status, location, admin count, and ID.
- Calls the protected first-admin assignment RPC for active colleges with no admins.

The Kotlin compilation could not be run in this environment because the Gradle wrapper distribution was not cached and network access to `services.gradle.org` was unavailable. Please sync/build in Android Studio and share any exact compiler error if one appears.
