#!/system/bin/sh
# Complet APK Installer - Magisk execution entrypoint
# Pure POSIX compatible (Android /system/bin/sh)

set -e

MODDIR="${MODPATH}/apks"
LOG="/data/local/tmp/magisk-apkinstall.log"
TMP="/data/local/tmp/magisk-apkinstall-tmp"

rm -f "$LOG"
mkdir -p "$TMP"

ui_print() { echo "• $*"; }
log()      { echo "$*" >> "$LOG"; }

ui_print "────────────────────────────────────────"
ui_print "   ✅ Complete Android APK Installer"
ui_print "────────────────────────────────────────"

if [ ! -d "$MODDIR" ]; then
  ui_print "❌ No apk directory found!"
  exit 0
fi

cd "$MODDIR"

count=0
total=$(ls *.apk *.apkm *.xapk 2>/dev/null | wc -l)

[ "$total" -eq 0 ] && {
    ui_print "⚠️  No APK/APKM/XAPK detected."
    exit 0
}

progress() {
    percent=$((($1 * 100) / $total))
    ui_print "Progress: $percent% ($1/$total)"
}

for file in *.apk *.apkm *.xapk; do
    [ -f "$file" ] || continue
    count=$((count + 1))
    progress "$count"

    log ">> Installing $file"

    case "$file" in
      *.apk)
          pm install -r -g "$file"
          ;;
      *.apkm|*.xapk)
          rm -rf "$TMP"
          mkdir -p "$TMP"
          unzip -o "$file" -d "$TMP" >>"$LOG" 2>&1

          SPLITS=$(ls "$TMP"/*.apk 2>/dev/null)
          if [ -n "$SPLITS" ]; then
             pm install-multiple -r -g $SPLITS
          else
             log "❌ No APKs found inside bundle"
          fi
          ;;
    esac

    status=$?
    if [ "$status" -eq 0 ]; then
        ui_print "✔ Installed $file"
        log "✔ Installed $file"
    else
        ui_print "✖ FAILED $file"
        log "✖ FAILED $file"
    fi
done

ui_print "✅ All installs completed."
log "✅ Finished"
exit 0
