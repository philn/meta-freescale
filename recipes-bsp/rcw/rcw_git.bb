SUMMARY = "Reset Configuration Word"
DESCRIPTION = "Reset Configuration Word - hardware boot-time parameters for the QorIQ targets"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://BSD-LICENSE;md5=627727dce58484c4bd5f9b19665d81b3"

DEPENDS += "change-file-endianess-native tcl-native"

inherit deploy

SRC_URI = "git://github.com/qoriq-open-source/rcw.git;nobranch=1"
SRCREV = "bfe8c5eb72cbae387af1c8662957caa801c2309f"

S = "${WORKDIR}/git"

export PYTHON = "${USRBINPATH}/python2"

EXTRA_OEMAKE = "BOARDS=${@d.getVar('MACHINE', True).replace('-64b','').replace('-32b','')} DESTDIR=${D}/boot/rcw/"

do_install () {
    oe_runmake install
    for f in `find ${D}/boot/rcw/ -name "*qspiboot*"`;do
        if echo $f |grep -q "qspiboot_sben"; then
            continue
        fi
        f_swap=`echo $f |sed -e 's/qspiboot/qspiboot_swap/'`
        tclsh ${STAGING_BINDIR_NATIVE}/byte_swap.tcl $f $f_swap 8
        mv -f $f_swap $f
    done
}

do_deploy () {
    install -d ${DEPLOYDIR}/rcw
    cp -r ${D}/boot/rcw/* ${DEPLOYDIR}/rcw/
}
addtask deploy after do_install

PACKAGES += "${PN}-image"
FILES_${PN}-image += "/boot"

COMPATIBLE_MACHINE = "(qoriq)"
PACKAGE_ARCH = "${MACHINE_ARCH}"
